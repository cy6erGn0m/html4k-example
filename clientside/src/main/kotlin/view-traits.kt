package cg.test

import market.model.OrderDirection
import market.model.OrderPlaceCommand
import market.model.UnknownCommand
import market.web.InstrumentPresenter
import java.util.*
import kotlin.properties.Delegates

trait QuoteRowViewModel {
    fun setInstrumentName(name: String)
    fun setValue(value: Double)
    fun setMove(move: QuoteMove)
}

trait QuotesTableViewModel {
    fun start(): Unit
    fun appendRowModel(row: QuoteRowViewModel)
    fun createRowModel(): QuoteRowViewModel
}

trait MainView {
    fun createQuotesTable(): QuotesTableViewModel
    fun createInstrumentView() : InstrumentView
}

trait InstrumentBlotterViewRow {
    var price : String
    var quantity : Int

    fun appendBefore(other : InstrumentBlotterViewRow)
    fun remove()
}

trait InstrumentBlotterView {
    fun clear()
    fun createRow(referenceRow : InstrumentBlotterViewRow?, init : (InstrumentBlotterViewRow) -> Unit) : InstrumentBlotterViewRow

    fun addPlaceholder()
}

trait InstrumentView {
    var presenter : InstrumentPresenter

    var instrumentTitle : String
    var buyVolume : Double
    var sellVolume : Double

    fun createBlotterViews() : Map<OrderDirection, InstrumentBlotterView>
}


trait WebSocketService {
    val socket: KWebSocket
    val orderListeners : MutableList<(Order) -> Unit>

    fun start() {
        socket.send(UnknownCommand)
    }

    fun sendOrderPlace(order: OrderPlaceCommand) {
        socket.send(order)
    }
}

trait KWebSocket {
    fun stop()
    fun send(o: Any)
}