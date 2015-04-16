package market.model


enum class OrderDirection {
    BUY SELL
}

val OrderDirection.opposite: OrderDirection
    get() = when (this) {
        OrderDirection.BUY -> OrderDirection.SELL
        OrderDirection.SELL -> OrderDirection.BUY
    }

val OrderDirection.comparisonSign: Int
    get() = when (this) {
        OrderDirection.BUY -> 1
        OrderDirection.SELL -> -1
    }

trait HasInstrument {
    val instrument: String

}

trait HasDirection {
    val direction: OrderDirection
}
