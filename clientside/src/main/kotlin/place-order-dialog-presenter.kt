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

    fun doValidate() : Boolean {
        var valid = true
        var tooltipShown = false

        view.hideTooltip()

        val price = view.price
        if (!price.matches("^[0-9]+(\\.[0-9]+)?$")) {
            valid = false
            view.priceValid = false

            view.showTooltip("Price should be number")
            tooltipShown = true
        } else {
            view.priceValid = true
        }

        val quantity = view.quantity
        if (!quantity.matches("^[0-9]+$") || quantity.toDouble0() == 0.0) {
            valid = false
            view.quantityValid = false

            if (!tooltipShown) {
                view.showTooltip("Quantity should be number")
                tooltipShown = true
            }
        } else {
            view.quantityValid = true
        }

        return valid
    }

    fun onAccepted() {
        if (doValidate()) {
            hide()
        }
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