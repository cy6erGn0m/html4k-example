package market.web

import market.model.OrderDirection
import java.util.HashMap

class InstrumentPresenter(val view : InstrumentView, val socket : WebSocketService) {
    init {
        view.presenter = this
    }

    private val collector = OrderCollector()
    private val blotterViews : Map<OrderDirection, InstrumentBlotterView>
    private val blotterPresenters : Map<OrderDirection, BlotterPresenter>

    init {
        blotterViews = view.createBlotterViews()
        blotterPresenters = blotterViews.mapValues { e -> BlotterPresenter(e.value) }
    }

    var currentInstrument : String = ""
        set(value) {
            $currentInstrument = value
            view.instrumentTitle = value
        }

    fun start() {
        updateVolume()
        blotterPresenters.values().forEach {
            it.start()
        }

        socket.orderListeners.add {
            when (it.state) {
                OrderState.ACTIVE -> onOrderUpdate(it)
                OrderState.CANCELLED -> onOrderCancelled(it)
                OrderState.COMPLETED -> onOrderCancelled(it) // TODO other behaviour for completion
                OrderState.UNKNOWN -> onOrderUpdate(it)
            }
        }
    }

    fun onOrderUpdate(order : Order) {
        if (order.quantity == 0) {
            onOrderCancelled(order)
            return
        }

        collector.putOrder(order)
        blotterPresenters[order.direction]!!.onOrderUpdate(order)

        updateVolume()
    }

    fun onOrderCancelled(order : Order) {
        collector.removeOrder(order)
        blotterPresenters[order.direction]!!.onOrderCancelled(order)

        updateVolume()
    }

    fun onPlaceOrderClicked() {
        val dialogView = view.createPlaceOrderDialog()
        val dialogPresenter = PlaceOrderDialogPresenter(dialogView, socket, currentInstrument, collector.buyOrders.map {it.price.toDouble0()}.min() ?: 1.0)

        dialogPresenter.show()
    }

    private fun updateVolume() {
        view.buyVolume = collector.buyVolume
        view.sellVolume = collector.sellVolume
    }
}

class OrderCollector {
    private val orders = HashMap<OrderDirection, MutableMap<String, Order>>(2)
    private var buy : Double = 0.0
    private var sell : Double = 0.0
    val buyOrders : List<Order>
        get() = ordersByDirection(OrderDirection.BUY)

    val sellOrders : List<Order>
        get() = ordersByDirection(OrderDirection.SELL)

    val buyVolume : Double
        get() = buy

    val sellVolume : Double
        get() = sell

    fun ordersByDirection(direction : OrderDirection) : List<Order> = orders[direction]?.values()?.toList() ?: emptyList()

    init {
        OrderDirection.values().forEach {
            orders.put(it, HashMap(1024))
        }
    }

    fun putOrder(order : Order) {
        orders[order.direction]!!.put(order.orderId, order)
        recalc()
    }

    fun removeOrder(order : Order) {
        orders.remove(order)
        recalc()
    }

    private fun recalc() {
        buy = orders[OrderDirection.BUY]!!.values().sum()
        sell = orders[OrderDirection.SELL]!!.values().sum()
    }

    private fun Collection<Order>.sum() = sumByDouble { it.quantity * it.price.toDouble0() }
}

fun String.toDouble0() = if (this.matches("^-?[0-9]+(\\.[0-9]+)?$".toRegex())) parseDouble(this) else throw IllegalArgumentException()
private fun parseDouble(s : String) : Double {
    var result = 0.0
    val sign = if (s.first() == '-') -1.0 else 1.0
    val parts = s.splitBy(".").map { if (it.startsWith("-")) it.substring(1) else it }

    parts[0].forEach {
        result = result * 10.0 + (it.toInt() - 0x30)
    }

    if (parts.size() > 1) {
        var div = 0.1
        parts[1].forEach { ch ->
            result += (ch.toInt() - 0x30) * div
            div *= 0.1
        }
    }

    return sign * result
}