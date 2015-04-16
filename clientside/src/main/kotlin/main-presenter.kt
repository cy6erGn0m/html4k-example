package cg.test

import market.model.OrderDirection
import market.web.InstrumentPresenter
import kotlin.properties.Delegates

class MainPresenter(val view : MainView, val webSocketService : WebSocketService) : QuotesListener {
    private val tableView by Delegates.lazy { view.createQuotesTable() }
    private val instrumentView by Delegates.lazy { view.createInstrumentView() }

    val tablePresenter by Delegates.lazy { QuotesTablePresenter(tableView) }
    val instrumentPresenter by Delegates.lazy { InstrumentPresenter(instrumentView) }

    fun start() {
        tableView.start()
        instrumentPresenter.currentInstrument = "INSTR-1"
        instrumentPresenter.start()
        webSocketService.start()
    }

    override fun onQuote(instrument: String, value: Double) {
        tablePresenter.onQuote(instrument, value)
    }
}