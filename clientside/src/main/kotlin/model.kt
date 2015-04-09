package cg.test

trait QuotesListener {
    fun onQuote(instrument : String, value : Double)
}

enum class QuoteMove {
    UP
    DOWN
    NEUTRAL
}

