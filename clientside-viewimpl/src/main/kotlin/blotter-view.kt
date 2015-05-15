package market.web.impl

import html4k.*
import html4k.dom.*
import html4k.injector.*
import market.web.*
import html4k.injector.InjectByClassName
import html4k.injector.InjectByTagName
import market.model.OrderDirection
import kotlin.dom.clear
import kotlin.dom.text
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.removeFromParent
import kotlin.properties.Delegates
import kotlin.reflect.KMutableMemberProperty

class InstrumentBlotterViewRowImpl(val parent : InstrumentBlotterViewImpl) : InstrumentBlotterViewRow {
    val row = document.create.tr {
        td {
        }
        td {
        }
    } as HTMLTableRowElement
    val priceCell = row.cells.item(0)!!
    val quantityCell = row.cells.item(1)!!

    override var price: String
        get() = priceCell.textContent!!
        set(value) {
            priceCell.textContent = value
        }

    override var quantity: Int
        get() = quantityCell.textContent!!.let { t -> if (t.matches("[0-9]+")) parseInt(t) else 0 }
        set(value) {
            quantityCell.textContent = value.toString()
        }

    fun append() {
        parent.tableBody.appendChild(row)
    }

    override fun appendBefore(other: InstrumentBlotterViewRow) {
        if (other !is InstrumentBlotterViewRowImpl) {
            throw UnsupportedOperationException()
        }

        parent.tableBody.insertBefore(row, other.row)
    }

    override fun remove() {
        row.removeFromParent()
    }
}

class InstrumentBlotterViewImpl : InstrumentBlotterView {
    var tableBody : HTMLTableSectionElement by Delegates.notNull()
    private val tableNode = createTable(::tableBody)

    val root : HTMLElement
        get() = tableNode

    override fun clear() {
        tableBody.clear()
    }

    override fun createRow(referenceRow : InstrumentBlotterViewRow?, init: (InstrumentBlotterViewRow) -> Unit): InstrumentBlotterViewRow {
        val row = InstrumentBlotterViewRowImpl(this)

        init(row)

        if (referenceRow == null) {
            row.append()
        } else {
            row.appendBefore(referenceRow)
        }

        return row
    }

    override fun addPlaceholder() {
        tableBody.appendPlaceholder()
    }

    private fun createTable(property : KMutableMemberProperty<InstrumentBlotterViewImpl, HTMLTableSectionElement>) =
        document.create.inject(this, listOf(InjectByTagName("tbody") to property)).table {
            classes = setOf("table", "table-condensed")
            thead {
                tr {
                    th {
                        +"Price"
                    }
                    th {
                        +"Qty"
                    }
                }
            }
            tbody {
            }
        }

    private fun HTMLTableSectionElement.appendPlaceholder() {
        append {
            tr {
                td {
                    colSpan = "2"
                    +"No orders"
                }
            }
        }
    }
}