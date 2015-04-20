package market.web

import cg.test.PlaceOrderDialogView
import cg.test.WebSocketService
import market.model.OrderDirection
import market.model.OrderPlaceCommand

class PlaceOrderDialogPresenter(val view : PlaceOrderDialogView, val socketService : WebSocketService, instrument : String, price : Double) {
    init {
        view.presenter = this

        view.instrumentName = instrument
        view.buyActive = true
        view.sellActive = false
        view.price = price.toString()
        view.quantity = "1"
    }

    private var currentDiction : OrderDirection = OrderDirection.BUY
        set(value) {
            $currentDiction = value

            view.buyActive = value == OrderDirection.BUY
            view.sellActive = value == OrderDirection.SELL
        }

    fun onQuantityUpClicked() {
        updateQuantity { it + 1 }
    }

    fun onQuantityDownClicked() {
        updateQuantity { Math.max(1, it - 1) }
    }

    fun onBuyButtonClicked() {
        currentDiction = OrderDirection.BUY
    }

    fun onSellButtonClicked() {
        currentDiction = OrderDirection.SELL
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

            val price = view.price
            val quantity = view.quantity.toDouble0().toInt()
            val direction = currentDiction

            socketService.sendOrderPlace(OrderPlaceCommand(view.instrumentName, price, quantity, direction.name()))
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