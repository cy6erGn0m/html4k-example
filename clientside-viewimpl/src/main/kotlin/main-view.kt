package cg.test.view.impl

import cg.test.*
import html4k.*
import html4k.dom.*
import market.model.OrderDirection
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLTableElement
import kotlin.js.dom.html.document
import kotlin.properties.Delegates

class MainViewModel : MainView {
    var quotesTableSlot : HTMLElement by Delegates.notNull()
    var blotterSlotBuy: HTMLElement by Delegates.notNull()
    var blotterSlotSell: HTMLElement by Delegates.notNull()

    override
    fun createQuotesTable() : QuotesTableViewModel {
        val view = createQuotesView()
        quotesTableSlot.appendChild(view.tableContainerElement)
        return view
    }

    override fun createBlotterView(direction : OrderDirection): InstrumentBlotterView {
        val blotterView = InstrumentBlotterViewImpl()

        val slot = when (direction) {
            OrderDirection.BUY -> blotterSlotBuy
            OrderDirection.SELL -> blotterSlotSell
        }

        slot.appendChild(blotterView.tableNode)

        return blotterView
    }
}

private fun createQuotesView() = QuotesTableViewModelImpl(document.create.quotesTable())
