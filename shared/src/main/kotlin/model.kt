package market.model


enum class OrderDirection {
    BUY, SELL
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

interface HasInstrument {
    val instrument: String

}

interface HasDirection {
    val direction: OrderDirection
}


interface InputCommand
data object UnknownCommand : InputCommand
data class OrderPlaceCommand(val instrument : String, val price : String, val quantity : Int, val direction : String, val type : String = "placeOrder") : InputCommand