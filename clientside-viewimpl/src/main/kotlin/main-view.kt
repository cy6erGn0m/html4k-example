package market.web.impl

import market.web.*
import html4k.*
import html4k.dom.*
import market.model.OrderDirection
import market.web.impl.InstrumentViewImpl
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTableElement
import kotlin.browser.document
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
