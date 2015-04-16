package market.events.socket

import kotlinx.jetsocket.Pipe
import kotlinx.jetsocket.WebSocket
import market.SimpleOrder
import market.events.ItemPlaced
import market.events.orderOnStack
import market.model.*
import market.servlets.m
import rx.Observable
import rx.schedulers.Schedulers
import java.math.BigDecimal
import java.util.Date
import javax.websocket.server.ServerEndpoint

class RawInputCommand(val type: String, val inst: String?, val p: String?, val qty: Int?, val bs: String?)

trait Event
data class Hello(val message: String, val type: String = "hello") : Event
data class OrderState(val id: Long, val instrument: String, val price: String, val quantity: Int, val direction: String, val type: String = "order") : Event

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
                                    id = it.orderOnStack.order.orderSign,
                                    instrument = it.orderOnStack.instrument,
                                    price = it.orderOnStack.order.price.toString(),
                                    quantity = it.orderOnStack.quantity,
                                    direction = it.orderOnStack.direction.name()
                            )
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
            "placeOrder" -> OrderPlaceCommand(command.inst!!, command.p!!, command.qty!!, command.bs!!)
            else -> UnknownCommand
        }