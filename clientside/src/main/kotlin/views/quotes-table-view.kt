package market.web.impl

import market.web.*
import html4k.injector.*
import html4k.TagConsumer
import html4k.js.div
import html4k.js.tr
import html4k.*
import html4k.dom.*
import org.w3c.dom.Node
import kotlin.dom.*
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.properties.Delegates

class QuoteRowViewModelImpl : QuoteRowViewModel {
    var root: HTMLTableRowElement by Delegates.notNull()

    var instrumentNameElement : HTMLElement by Delegates.notNull()
    var valueSpanElement : HTMLElement by Delegates.notNull()
    var iconElement : HTMLElement by Delegates.notNull()

    override
    fun setInstrumentName(name: String) {
        instrumentNameElement.textContent = name
    }

    override fun setValueAndMove(value: Double, move: QuoteMove) {
        root.classes = rowClassFor(move)
        root.getElementsByClassName("move-i").asList().forEach {
            it.classes = "move-i glyphicon " + iconClassFor(move)
        }
        root.getElementsByClassName("value-span").asList().forEach {
            it.textContent = value.toString()
            it.parentElement?.classes = textClassFor(move)
        }
    }
}

private fun createRow() = with(QuoteRowViewModelImpl()) { document.create.inject(this, listOf(
        InjectRoot to QuoteRowViewModelImpl::root,
        InjectByClassName("value-span") to QuoteRowViewModelImpl::valueSpanElement,
        InjectByClassName("name-cell") to QuoteRowViewModelImpl::instrumentNameElement,
        InjectByClassName("move-i") to QuoteRowViewModelImpl::iconElement
)).createQuoteRow(); this }

class QuotesTableViewModelImpl(val tableContainerElement: HTMLElement) : QuotesTableViewModel {
    private val tbodyElement = tableContainerElement.getElementsByTagName("tbody").asList().first()
    private val placeholder = document.create.tr {
        classes = setOf("warning")
        td {
            attributes["colspan"] = "3"

            +"Instruments not yet loaded"
        }
    }

    override
    fun start() {
        if (tbodyElement.childNodes.length == 0) {
            tbodyElement.appendChild(placeholder)
        }
    }

    override
    fun appendRowModel(row : QuoteRowViewModel) {
        placeholder.removeFromParent()
        tbodyElement.appendChild((row as QuoteRowViewModelImpl).root)
    }

    override
    fun createRowModel() : QuoteRowViewModel = createRow()
}

fun TagConsumer<HTMLElement>.quotesTable(): HTMLElement =
        div {
            classes = setOf("panel", "panel-default")

            div {
                classes = setOf("panel-heading")

            }

            table {
                classes = setOf("table table-condensed")

                thead {
                    tr {
                        th { }
                        th { +"Instrument" }
                        th { +"Value" }
                    }
                }

                tbody {

                }
            }
        }

private fun TagConsumer<HTMLElement>.createQuoteRow(): HTMLTableRowElement =
        tr {
            td {
                i {
                    classes = setOf("move-i")
                }
            }
            td {
                classes = setOf("name-cell")
            }
            td {
                span {
                    classes = setOf("value-span")
                    +""
                }
                +"¤"
            }
        }

private fun textClassFor(move: QuoteMove): String {
    return when (move) {
        QuoteMove.UP -> "text-success"
        QuoteMove.DOWN -> "text-danger"
        QuoteMove.NEUTRAL -> "text-muted"
    }
}

private fun iconClassFor(move: QuoteMove): String {
    return when (move) {
        QuoteMove.UP -> "glyphicon-arrow-up"
        QuoteMove.DOWN -> "glyphicon-arrow-down"
        QuoteMove.NEUTRAL -> "glyphicon-minus"
    }
}

private fun rowClassFor(move: QuoteMove): String {
    return when (move) {
        QuoteMove.UP -> "success"
        QuoteMove.DOWN -> "danger"
        QuoteMove.NEUTRAL -> ""
    }
}

