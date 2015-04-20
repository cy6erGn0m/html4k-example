package market.model.server

import market.model.*
import java.math.BigDecimal

data class OrderTrade(
        override val instrument: String,
        val price: BigDecimal,
        val quantity: Int
) : HasInstrument


trait Order : HasInstrument, HasDirection {
    val price: BigDecimal
    val quantity: Int
    val orderSign: Long
}

data class OrderOnStack(val order: Order, var quantity : Int = order.quantity) : Comparable<OrderOnStack>, HasInstrument by order, HasDirection by order {

    override fun compareTo(other: OrderOnStack): Int =
            order.price.compareTo(other.order.price).let {
                if (it != 0) it else order.orderSign.compareTo(other.order.orderSign)
            }
}

fun Order.onStack() = OrderOnStack(this)
