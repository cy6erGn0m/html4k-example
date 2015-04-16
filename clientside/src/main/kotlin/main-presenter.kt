package cg.test

import market.model.OrderDirection
import kotlin.properties.Delegates

class MainPresenter(val view : MainView, val webSocketService : WebSocketService) : QuotesListener {
    private val tableView : QuotesTableViewModel by Delegates.lazy { view.createQuotesTable() }
    private val blotterViews : List<InstrumentBlotterView>

    val tablePresenter by Delegates.lazy { QuotesTablePresenter(tableView) }
    val blotterPresenters : List<BlotterPresenter>

    init {
        blotterViews = OrderDirection.values().map { view.createBlotterView(it) }
        blotterPresenters = blotterViews.map { view -> BlotterPresenter(view) }
    }

    fun start() {
        tableView.start()

        blotterPresenters.forEach { it.start() }

        webSocketService.start()
    }

    override fun onQuote(instrument: String, value: Double) {
        tablePresenter.onQuote(instrument, value)
    }
}