package market

import market.model.*
import market.model.server.*
import market.events.*
import market.util.grouping.GroupingHandler
import rx.Observable
import rx.Observer
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.math.BigDecimal
import java.util.ArrayList
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.name

private class Blotter(override val instrument: String) : HasInstrument, Observer<ItemEvent<Order>> {
    private val tree = TreeSet<OrderOnStack>()
    private val ordersSubject = PublishSubject.create<ItemEvent<OrderOnStack>>()
    private val tradesSubject = PublishSubject.create<ItemEvent<OrderTrade>>()

    val orders: Observable<ItemEvent<OrderOnStack>> = ordersSubject
    val trades : Observable<ItemEvent<OrderTrade>> = tradesSubject

    val oppositeObserver = PublishSubject.create<ItemEvent<OrderOnStack>>()

    init {
        oppositeObserver
                .filter { it is ItemPlaced }.
                forEach {
                    processOrder(it.orderOnStack)
                }
    }

    override fun onNext(event: ItemEvent<Order>) {
        when (event) {
            is ItemPlaced -> put(event.order)
            is ItemCancelled -> remove(event.order)
        }
    }

    override fun onError(e: Throwable?) {
    }

    override fun onCompleted() {
    }

    private fun put(order: Order) {
        val orderState = order.onStack()
        tree.add(orderState)
        ordersSubject.onNext(ItemPlaced(orderState))
    }

    private fun remove(order: Order) {
        if (!tree.remove(order.onStack())) {
            val iterator = tree.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().order === order) {
                    iterator.remove()
                }
            }
        }
    }

    private fun processOrder(sourceItem: OrderOnStack) {
        val direction = sourceItem.order.direction
        val maxPrice = sourceItem.order.price

        var changed = false
        val it = if (direction == OrderDirection.BUY) tree.iterator() else tree.descendingIterator()
        while (sourceItem.quantity > 0 && it.hasNext()) {
            val item = it.next()
            assert(item !== sourceItem) { "We found sourceItem on contrary side blotter" }

            if (item.order.price.compareTo(maxPrice) == direction.comparisonSign) {
                break
            }

            val toBuy = Math.min(item.quantity, sourceItem.quantity)
            assert(toBuy > 0) { "there is item with qty = 0 stuck here" }
            sourceItem.quantity -= toBuy
            item.quantity -= toBuy
            changed = true
            tradesSubject.onNext(ItemPlaced(OrderTrade(item.order.instrument, item.order.price, toBuy)))

            if (item.quantity == 0) {
                it.remove()
                ordersSubject.onNext(ItemCompleted(item))
            } else {
                ordersSubject.onNext(ItemChanged(item))
            }
        }

        if (sourceItem.quantity == 0) {
            ordersSubject.onNext(ItemCompleted(sourceItem))
        } else if (changed) {
            ordersSubject.onNext(ItemChanged(sourceItem))
        }
    }
}

public class Market {
    private val stackSubject = PublishSubject.create<ItemEvent<OrderOnStack>>()
    private val ordersSubject = PublishSubject.create<ItemEvent<Order>>()

    val ordersOnStack: Observable<ItemEvent<OrderOnStack>> = stackSubject
    val orderObserver : Observer<ItemEvent<Order>> = ordersSubject
    val trades = PublishSubject.create<ItemEvent<OrderTrade>>()

    init {
        ordersSubject.
                observeOn(Schedulers.newThread()).
                groupBy { it.order.instrument }.
                forEach { group ->
                    val buyBlotter = Blotter(group.getKey())
                    val sellBlotter = Blotter(group.getKey())

                    buyBlotter.orders.subscribe(sellBlotter.oppositeObserver)
                    sellBlotter.orders.subscribe(buyBlotter.oppositeObserver)

                    buyBlotter.orders.subscribe(stackSubject)
                    sellBlotter.orders.subscribe(stackSubject)

                    buyBlotter.trades.subscribe(trades)
                    sellBlotter.trades.subscribe(trades)

                    group.observeOn(Schedulers.newThread()).
                            groupBy {it.order.direction}.
                            forEach { directionGroup ->
                                val blotter = when (directionGroup.getKey()) {
                                    OrderDirection.BUY -> buyBlotter
                                    OrderDirection.SELL -> sellBlotter
                                    else -> throw IllegalArgumentException("unsupported direction ${directionGroup.getKey()}")
                                }

                                directionGroup.subscribe(blotter)
                            }
                }
    }

    fun stop() {
        stackSubject.onCompleted()
        ordersSubject.onCompleted()
    }
}