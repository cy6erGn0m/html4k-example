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

data class Order(val orderId : String, val instrument : String, val price : String, val quantity : Int, val direction: OrderDirection)