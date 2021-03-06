package market

import market.model.*
import market.model.server.Order
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

private val counter = AtomicLong(Long.MIN_VALUE)
data class SimpleOrder(
        override val instrument : String,
        override val price : BigDecimal,
        override val quantity : Int,
        override val direction : OrderDirection
) : Order {
    override val orderSign: Long = counter.incrementAndGet()
}
