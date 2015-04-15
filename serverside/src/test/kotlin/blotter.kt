package market.test

import market.*
import market.events.ItemEvent
import market.events.ItemPlaced
import market.events.orderOnStack
import market.events.trade
import org.junit.After
import org.junit.Ignore
import rx.Observer
import rx.schedulers.Schedulers
import rx.subjects.TestSubject
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.assertEquals
import org.junit.Test as test

fun order(instrument : String, price : Double, quantity : Int, direction : OrderDirection) = SimpleOrder(instrument, BigDecimal.valueOf(price), quantity, direction)
fun Observer<Order>.buy(instrument: String, price: Double, quantity: Int) = this.onNext(order(instrument, price, quantity, OrderDirection.BUY))
fun Observer<Order>.sell(instrument: String, price: Double, quantity: Int) = this.onNext(order(instrument, price, quantity, OrderDirection.SELL))

class BlotterTest {
    val trades = CopyOnWriteArrayList<OrderTrade>()
    val market = Market()

    After
    fun finish() {
        market.stop()
    }

    test fun mmm() {
        market.ordersOnStack.forEach {
            println(it.orderOnStack)
        }
        market.trades.forEach {
            println(it.trade)
        }

        val scheduler = Schedulers.test()
        val s = TestSubject.create<Order>(scheduler)
        s.map { ItemPlaced(it) }.subscribe(market.orderObserver)

        s.sell("A", 10.0, 1)
        s.buy("A", 9.0, 2)

        scheduler.triggerActions()
        Thread.sleep(1000)
        println("should be no trade yet")

        s.sell("A", 9.0, 1)

        scheduler.triggerActions()
        Thread.sleep(1000)
    }

    Ignore
    test fun simple() {
//        market.sell("A", 10.0, 2)
//        market.buy("A", 9.0, 2)
//        market.sell("A", 9.0, 1)

        Thread.sleep(1000)
        market.stop()

        assertEquals(listOf(OrderTrade("A", BigDecimal.valueOf(9.0), 1)), trades)
    }
}