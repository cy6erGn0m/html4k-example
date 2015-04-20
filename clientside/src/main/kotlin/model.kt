package cg.test

import market.model.*

trait QuotesListener {
    fun onQuote(instrument : String, value : Double)
}

enum class QuoteMove {
    UP
    DOWN
    NEUTRAL
}

enum class OrderState {
    ACTIVE
    COMPLETED
    CANCELLED
    UNKNOWN
}

data class Order(val orderId : String, val instrument : String, val price : String, val quantity : Int, val direction: OrderDirection, state : OrderState) {
    val state : OrderState = state
}