package market.web.impl

import cg.test.KWebSocket
import cg.test.Order
import cg.test.WebSocketService
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

    private fun onMessage(o: dynamic) {
        if (o.type == "order") {
            val order = Order(o.id, o.instrument, o.price, o.quantity, OrderDirection.valueOf(o.direction))
            orderListeners.forEach { it(order) }
        }
    }
}