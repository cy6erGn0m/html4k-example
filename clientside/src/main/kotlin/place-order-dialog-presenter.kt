package market.web

import cg.test.PlaceOrderDialogView
import market.model.OrderDirection

class PlaceOrderDialogPresenter(val view : PlaceOrderDialogView, instrument : String, price : Double) {
    init {
        view.presenter = this

        view.instrumentName = instrument
        view.buySell = OrderDirection.BUY
        view.price = price.toString()
        view.quantity = "1"
    }

    fun onQuantityUpClicked() {
        updateQuantity { it + 1 }
    }

    fun onQuantityDownClicked() {
        updateQuantity { Math.max(1, it - 1) }
    }

    fun show() {
        view.show()
    }

    fun hide() {
        view.hide()
    }

    fun doValidate() {
    }

    private fun updateQuantity(block : (Int) -> Int) {
        val quantityText = view.quantity
        try {
            val value = quantityText.toDouble0().toInt()
            view.quantity = block(value).toString()
        } catch(expected : IllegalArgumentException) {
        }
    }
}