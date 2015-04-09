package cg.test

import kotlin.js.dom.html.document

class MainViewModel {
    private val quotesTableSlot = document.getElementById("quotes-slot")!!

    fun createQuotesTable() : QuotesTableViewModel {
        val view = createQuotesView()
        quotesTableSlot.appendChild(view.tableContainerElement)
        return view
    }
}
private fun createQuotesView() : QuotesTableViewModel = QuotesTableViewModel(document.createTree0().quotesTable())
