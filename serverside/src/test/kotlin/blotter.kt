package market.test

import market.*
import market.events.place
import org.junit.After
import java.math.BigDecimal
import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.assertEquals
import org.junit.Test as test

class DummyOrder(
        override val instrument : String,
        override val price : BigDecimal,
        override val quantity : Int,
        override val direction : OrderDirection,
        override val orderSign : Long
                 ) : Order

val counter = AtomicLong()
fun order(instrument : String, price : Double, quantity : Int, direction : OrderDirection) = DummyOrder(instrument, BigDecimal.valueOf(price), quantity, direction, counter.incrementAndGet())
fun Market.buy(instrument: String, price: Double, quantity: Int) = this.place(order(instrument, price, quantity, OrderDirection.BUY))
fun Market.sell(instrument: String, price: Double, quantity: Int) = this.place(order(instrument, price, quantity, OrderDirection.SELL))

class BlotterTest {
    val trades = CopyOnWriteArrayList<OrderTrade>()
    val market = Market(object : TradeListener {
        override fun onTrade(trade: OrderTrade) {
            println("traded")
            trades.add(trade)
        }
    })

    After
    fun finish() {
        market.stop()
    }

    test fun simple() {
        market.sell("A", 10.0, 2)
        market.buy("A", 9.0, 2)
        market.sell("A", 9.0, 1)

        Thread.sleep(1000)
        market.stop()

        assertEquals(listOf(OrderTrade("A", BigDecimal.valueOf(9.0), 1)), trades)
    }
}