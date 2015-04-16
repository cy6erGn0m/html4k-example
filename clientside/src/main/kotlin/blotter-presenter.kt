package cg.test

import market.model.OrderDirection
import java.util.*

class BlotterPresenter(val view : InstrumentBlotterView) {
    // orderId -> row
    private val rows = LinkedHashMap<String, InstrumentBlotterViewRow>()

    fun start() {
        view.addPlaceholder()
    }

    fun putOrderState(order : Order) {
        if (rows.isEmpty()) {
            view.clear() // to remove placeholder
        }

        // note: since order price couldn't be changed so we haven't reorder rows, just insert at valid place
        val orderRow = rows[order.orderId]
        if (orderRow == null) {
            val referencePrice = if (order.direction == OrderDirection.BUY) rows.values().map {it.price}.max() else rows.values().map {it.price}.min()
            val referenceRow : InstrumentBlotterViewRow? = rows.values().filter {it.price == referencePrice}.lastOrNull()

            rows.put(order.orderId, view.createRow(referenceRow) { updateOrderRow(it, order) })
        } else {
            updateOrderRow(orderRow, order)
        }
    }

    fun cancelOrder(order : Order) {
        rows.remove(order.orderId)?.remove()

        if (rows.isEmpty()) {
            view.addPlaceholder()
        }
    }

    private fun updateOrderRow(row : InstrumentBlotterViewRow, order : Order) {
        row.price = order.price
        row.quantity = order.quantity
    }
}
