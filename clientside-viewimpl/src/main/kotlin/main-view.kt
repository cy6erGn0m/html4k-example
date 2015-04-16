package cg.test.view.impl

import cg.test.*
import html4k.*
import html4k.dom.*
import market.model.OrderDirection
import market.web.impl.InstrumentViewImpl
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLTableElement
import kotlin.js.dom.html.document
import kotlin.properties.Delegates

class MainViewModel : MainView {
    var quotesTableSlot : HTMLElement by Delegates.notNull()
    var instrumentViewSlot : HTMLElement by Delegates.notNull()

    override
    fun createQuotesTable() : QuotesTableViewModel {
        val view = createQuotesView()
        quotesTableSlot.appendChild(view.tableContainerElement)
        return view
    }

    override fun createInstrumentView(): InstrumentView {
        val view = InstrumentViewImpl()
        instrumentViewSlot.appendChild(view.root)
        return view
    }
}

private fun createQuotesView() = QuotesTableViewModelImpl(document.create.quotesTable())
