package cg.test

import kotlin.properties.Delegates

class MainPresenter(val view : MainView) : QuotesListener {
    private val tableView : QuotesTableViewModel by Delegates.lazy { view.createQuotesTable() }
    val tablePresenter by Delegates.lazy { QuotesTablePresenter(tableView) }

    fun start() {
        tableView.start()
    }

    override fun onQuote(instrument: String, value: Double) {
        tablePresenter.onQuote(instrument, value)
    }
}