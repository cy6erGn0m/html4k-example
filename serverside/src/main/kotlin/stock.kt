package market

import market.events.*
import market.util.grouping.GroupingHandler
import market.util.grouping.toEventListener
import java.math.BigDecimal
import java.util.ArrayList
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

trait HasInstrument {
    val instrument : String
}

trait HasDirection {
    val direction : OrderDirection
}

data class OrderTrade (
    override val instrument : String,
    val price : BigDecimal,
    val quantity : Int
) : HasInstrument

enum class OrderDirection {
    BUY SELL
}

val OrderDirection.opposite : OrderDirection
    get() = when (this) {
        OrderDirection.BUY -> OrderDirection.SELL
        OrderDirection.SELL -> OrderDirection.BUY
    }

val OrderDirection.comparisonSign : Int
    get() = when (this) {
        OrderDirection.BUY -> 1
        OrderDirection.SELL -> -1
    }

trait Order : HasInstrument, HasDirection {
    val price : BigDecimal
    val quantity : Int
    val orderSign : Long
}

data class OrderOnStack(val order : Order) : Comparable<OrderOnStack>, HasInstrument by order, HasDirection by order {

    var quantity : Int = order.quantity

    override fun compareTo(other: OrderOnStack): Int =
        order.price.compareTo(other.order.price).let {
            if (it != 0) it else order.orderSign.compareTo(other.order.orderSign)
        }
}

fun Order.onStack() = OrderOnStack(this)

private class Blotter(listener : ItemEventListener<OrderOnStack>, override val instrument : String) : HasInstrument {
    private val listener = listener
    private val tree = TreeSet<OrderOnStack>()

    fun put(order : Order) {
        val orderState = OrderOnStack(order)
        tree.add(orderState)
        listener.onEvent(ItemPlaced(orderState))
    }

    fun remove(order : Order) {
        if (!tree.remove(order.onStack())) {
            val iterator = tree.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().order === order) {
                    iterator.remove()
                }
            }
        }
    }

    fun minus(sourceItem : OrderOnStack) : List<OrderTrade> {
        val completed = ArrayList<OrderOnStack>(Math.min(tree.size(), 16))
        val trades = ArrayList<OrderTrade>()
        val direction = sourceItem.order.direction
        val maxPrice = sourceItem.order.price

        val it = if (direction == OrderDirection.SELL) tree.iterator() else tree.descendingIterator()
        while (sourceItem.quantity > 0 && it.hasNext()) {
            val item = it.next()
            if (item.order.price.compareTo(maxPrice) == direction.comparisonSign) {
                break
            }

            val toBuy = Math.min(item.quantity, sourceItem.quantity)
            sourceItem.quantity -= toBuy
            item.quantity -= toBuy
            trades.add(OrderTrade(item.order.instrument, item.order.price, toBuy))

            if (item.quantity == 0) {
                it.remove()
                completed.add(item)
            }
        }

        if (sourceItem.quantity == 0) {
            completed.add(sourceItem)
        }

        completed.forEach {
            listener.onEvent(ItemCompleted(it))
        }

        return trades
    }
}

fun <T : HasInstrument> InstrumentGrouping(workers : Int = Runtime.getRuntime().availableProcessors(), handler : (ItemEvent<T>) -> Unit) = GroupingHandler(workers, {e -> e.item.instrument}, handler)

public class Market(tradeListener : TradeListener) : ItemEventListener<Order> {
    private val blotters = ConcurrentHashMap<Pair<String, OrderDirection>, Blotter>()
    private val exec = InstrumentGrouping<OrderOnStack> { event ->
        val oppositeBlotter = getBlotter(event.orderOnStack.instrument, event.orderOnStack.order.direction.opposite)

        when (event) {
            is ItemPlaced -> oppositeBlotter.minus(event.orderOnStack).forEach { tradeListener.onTrade(it) }
            is ItemCancelled -> println("order cancelled")
            is ItemCompleted -> println("order completed")
            else -> println("Got event $event")
        }
    }

    override fun onEvent(event: ItemEvent<Order>) {
        val blotter = getBlotter(event.order.instrument, event.order.direction)

        when (event) {
            is ItemPlaced -> blotter.put(event.order)
            is ItemCancelled -> blotter.remove(event.order)
        }
    }

    fun stop() {
        exec.stop()
        exec.join()
    }

    tailRecursive
    private fun getBlotter(instrument : String, direction : OrderDirection) : Blotter {
        val first = blotters.get(instrument to direction)
        if (first != null) {
            return first
        }

        blotters.putIfAbsent(instrument to direction, Blotter(exec.toEventListener(), instrument))
        return getBlotter(instrument, direction)
    }
}