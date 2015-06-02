package market.web

import kotlin.properties.Delegates

class MainPresenter(val view : MainView, val webSocketService : WebSocketService) {
    private val tableView by Delegates.lazy { view.createQuotesTable() }
    private val instrumentView by Delegates.lazy { view.createInstrumentView() }

    val tablePresenter by Delegates.lazy { QuotesTablePresenter(tableView) }
    val instrumentPresenter by Delegates.lazy { InstrumentPresenter(instrumentView, webSocketService) }

    fun start() {
        tableView.start()
        instrumentPresenter.currentInstrument = "INSTR-1"
        instrumentPresenter.start()
        webSocketService.quoteListeners.add {
            if (it.instrument.matches(".*\\-[0-9]$".toRegex())) {
                onQuote(it.instrument, parseDouble(it.value))
            }
        }

        webSocketService.start()
    }

    private fun onQuote(instrument: String, value: Double) {
        tablePresenter.onQuote(instrument, value)
    }
}