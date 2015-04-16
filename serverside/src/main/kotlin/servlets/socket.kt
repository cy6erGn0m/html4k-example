package market.events.socket

import kotlinx.jetsocket.Pipe
import kotlinx.jetsocket.WebSocket
import rx.Observable
import rx.schedulers.Schedulers
import java.util.Date
import javax.websocket.server.ServerEndpoint

trait InputCommand
data class CreateOrder(val instrument: String, val price: String, val quantity: Int, val direction: String) : InputCommand

trait Event
data class Hello(val message: String) : Event
data class OrderState(val id: Long, val instrument: String, val price: String, val quantity: Int, val direction: String) : Event

ServerEndpoint("/ws")
class MySocket : WebSocket<InputCommand, Event>(javaClass<InputCommand>(), { socket ->
    socket.observeOn(Schedulers.newThread()).map { request ->
        Pipe(request,
                Observable.concat<Event>(
                        Observable.just(Hello("Hello from server at ${Date()}")),
                        request.input.map {
                            Hello("You have entered ${it}")
                        }
                )
        )
    }
})

