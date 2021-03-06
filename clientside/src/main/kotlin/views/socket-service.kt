package market.web.impl

import market.web.*
import market.model.OrderDirection
import market.model.OrderPlaceCommand
import java.util.*
import kotlin.properties.Delegates


class WebSocketServiceImpl(val factory: ((dynamic) -> Unit) -> KWebSocket) : WebSocketService {

    override val socket: KWebSocket by Delegates.lazy {
        factory {
            onMessage(it)
        }
    }

    override val orderListeners = ArrayList<(Order) -> Unit>()
    override val quoteListeners = ArrayList<(Quote) -> Unit>()

    private fun onMessage(o: dynamic) {
        if (o.type == "order") {
            val order = Order(o.id, o.instrument, o.price, o.quantity, OrderDirection.valueOf(o.direction), OrderState.valueOf(o.state.toUpperCase()))
            orderListeners.forEach { it(order) }
        } else if (o.type == "quote") {
            val quote = Quote(o.instrument, o.value)
            quoteListeners.forEach { it(quote) }
        }
    }
}