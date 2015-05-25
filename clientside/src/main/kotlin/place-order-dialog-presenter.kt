package market.web


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

    private val shownTooltips = hashSetOf<PlaceOrderDialogFieldAnchor>()

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
        view.hideTooltips()
        view.hide()
    }

    fun doValidate() : Boolean {
        var valid = true

        val price = view.price
        if (!price.matches("^[0-9]+(\\.[0-9]+)?$".toRegex())) {
            valid = false
            view.priceValid = false

            if (PlaceOrderDialogFieldAnchor.PRICE !in shownTooltips) {
                view.showTooltip(PlaceOrderDialogFieldAnchor.PRICE, "Price should be number")
                shownTooltips.add(PlaceOrderDialogFieldAnchor.PRICE)
            }
        } else {
            view.priceValid = true
            shownTooltips.remove(PlaceOrderDialogFieldAnchor.PRICE)
            view.hideTooltip(PlaceOrderDialogFieldAnchor.PRICE)
        }

        val quantity = view.quantity
        if (!quantity.matches("^[0-9]+$".toRegex()) || quantity.toDouble0() == 0.0) {
            valid = false
            view.quantityValid = false

            if (PlaceOrderDialogFieldAnchor.QUANTITY !in shownTooltips) {
                view.showTooltip(PlaceOrderDialogFieldAnchor.QUANTITY, "Quantity should be number")
                shownTooltips.add(PlaceOrderDialogFieldAnchor.QUANTITY)
            }
        } else {
            view.quantityValid = true
            view.hideTooltip(PlaceOrderDialogFieldAnchor.QUANTITY)
            shownTooltips.remove(PlaceOrderDialogFieldAnchor.QUANTITY)
        }

        view.setPlaceOrderEnabled(valid)

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

    fun onCancelled() {
        hide()
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