package market.events.socket

import kotlinx.jetsocket.Pipe
import kotlinx.jetsocket.ServletContextListener
import kotlinx.jetsocket.WebSocket
import market.SimpleOrder
import market.events.*
import market.model.*
import market.model.server.*
import market.servlets.m
import market.web.impl.initTicksSubject
import market.web.impl.ticksSubject
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import java.math.BigDecimal
import java.util.Date
import javax.servlet.ServletContextEvent
import javax.servlet.annotation.WebListener
import javax.websocket.server.ServerEndpoint

class RawInputCommand(val type: String?, val instrument: String?, val price: String?, val quantity: Int?, val direction: String?)

interface Event
data class Hello(val message: String, val type: String = "hello") : Event
data class OrderState(val id: String, val instrument: String, val price: String, val quantity: Int, val direction: String, val state : String, val type: String = "order") : Event
data class QuoteEvent(val instrument: String, val value: String, val type: String = "quote") : Event

ServerEndpoint("/ws")
class MySocket : WebSocket<RawInputCommand, Event>(javaClass<RawInputCommand>(), { socket ->
    socket.observeOn(Schedulers.newThread()).map { request ->
        println("Socket connected ${request.session.getId()}")
        Pipe(request,
                Observable.merge<Event>(
                        Observable.concat<Event>(
                                Observable.just(Hello("Hello from server at ${Date()}")),
                                request.input.map(::parseInputCommand).flatMap {
                                    if (it is OrderPlaceCommand) {
                                        handleOrderPlaceCommand(it)
                                    } else {
                                        Observable.empty<Event>()
                                    }
                                }
                        ),
                        m.ordersOnStack.map {
                            OrderState(
                                    id = it.orderOnStack.order.orderSign.toString(),
                                    instrument = it.orderOnStack.instrument,
                                    price = it.orderOnStack.order.price.toString(),
                                    quantity = it.orderOnStack.quantity,
                                    direction = it.orderOnStack.direction.name(),
                                    state = when (it) {
                                        is ItemPlaced -> "active"
                                        is ItemChanged -> "active"
                                        is ItemCompleted -> "completed"
                                        is ItemCancelled -> "cancelled"
                                        else -> "unknown"
                                    }
                            )
                        },
                        ticksSubject.map {
                            QuoteEvent(it.instrument, it.value.toString())
                        }
                )
        )
    }
})

private fun handleOrderPlaceCommand(command: OrderPlaceCommand): Observable<Event> {
    val order = SimpleOrder(command.instrument, BigDecimal(command.price), command.quantity, OrderDirection.valueOf(command.direction))

    m.orderObserver.onNext(ItemPlaced<Order>(order))

    return Observable.empty()
}

private fun parseInputCommand(command: RawInputCommand): InputCommand =
        when (command.type) {
            "placeOrder" -> OrderPlaceCommand(command.instrument!!, command.price!!, command.quantity!!, command.direction!!)
            else -> UnknownCommand
        }

WebListener
class SocketInitializer : ServletContextListener() {
    private volatile var subscription: Subscription? = null

    override fun contextInitialized(sce: ServletContextEvent?) {
        subscription = initTicksSubject()
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        subscription?.unsubscribe()
    }
}