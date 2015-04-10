package cg.test.view.impl

import cg.test.*
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLTableElement
import kotlin.js.dom.html.document
import kotlin.properties.Delegates

class MainViewModel : MainView {
    var quotesTableSlot : HTMLElement by Delegates.notNull()

    override
    fun createQuotesTable() : QuotesTableViewModel {
        val view = createQuotesView()
        quotesTableSlot.appendChild(view.tableContainerElement)
        return view
    }
}

private fun createQuotesView() = QuotesTableViewModelImpl(document.createTree0().quotesTable())
