package cg.test

import html4k.TagConsumer
import html4k.js.div
import html4k.js.tr
import org.w3c.dom.Node
import kotlin.dom.classes
import kotlin.dom.first
import kotlin.js.dom.html.*

class QuoteRowViewModel(val element: HTMLTableRowElement) {
    private val instrumentNameElement = element.cells.item(1) as HTMLElement
    private val valueSpanElement = element.getElementsByClassName("value-span").item(0) as HTMLElement
    private val iconElement = element.getElementsByClassName("move-i").item(0) as HTMLElement

    fun setInstrumentName(name: String) {
        instrumentNameElement.textContent = name
    }

    fun setValue(value: Double) {
        valueSpanElement.textContent = value.toString()
    }

    fun setMove(move: QuoteMove) {
        element.classes = rowClassFor(move)
        valueSpanElement.classes = textClassFor(move)
        iconElement.classes = "move-i glyphicon " + iconClassFor(move)
    }
}

class QuotesTableViewModel(val tableContainerElement: HTMLElement) {
    private val tbodyElement = tableContainerElement.getElementsByTagName("tbody").first!!
    private val placeholder = tableContainerElement.createTree0().tr0 {
        classes = setOf("warning")
        td {
            attributes["colspan"] = "3"

            +"Instruments not yet loaded"
        }
    }

    fun start() {
        if (tbodyElement.childNodes.length == 0) {
            tbodyElement.appendChild(placeholder)
        }
    }

    fun appendRowModel(row : QuoteRowViewModel) {
        placeholder.removeFromParent()
        tbodyElement.appendChild(row.element)
    }

    // we actually don't need it
//    fun removeRowModel(row : QuoteRowViewModel) {
//        tbodyElement.removeChild(row.element)
//    }

    fun createRowModel() = QuoteRowViewModel(document.createTree0().createQuoteRow())
}

fun TagConsumer<HTMLElement>.quotesTable(): HTMLElement =
        div0 {
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
        tr0 {
            td {
                i {
                    classes = setOf("move-i")
                }
            }
            td {
            }
            td {
                span {
                    classes = setOf("value-span")
                    +""
                }
                +"¤"
            }
        }

fun HTMLTableRowElement.modify(value: Double, move: QuoteMove) {
    classes = rowClassFor(move)
    getElementsByClassName("move-i").forEachElement {
        it.classes = "move-i glyphicon " + iconClassFor(move)
    }
    getElementsByClassName("value-span").forEachElement {
        it.textContent = value.toString()
        (it.parentNode as HTMLElement).classes = textClassFor(move)
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

fun Node.removeFromParent() : Unit { parentNode?.removeChild(this) }
