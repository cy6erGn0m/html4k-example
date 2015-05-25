package market.web.impl

import market.web.*
import html4k.*
import html4k.js.*
import html4k.dom.*
import html4k.injector.*
import market.model.OrderDirection
import market.web.InstrumentPresenter
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.properties.Delegates

class InstrumentViewImpl : InstrumentView {
    override var presenter: InstrumentPresenter by Delegates.notNull()

    private var nameSpan: HTMLElement by Delegates.notNull()
    private var buyVolumeSpan: HTMLElement by Delegates.notNull()
    private var sellVolumeSpan : HTMLElement by Delegates.notNull()
    private var blotterSlotBuy: HTMLElement by Delegates.notNull()
    private var blotterSlotSell: HTMLElement by Delegates.notNull()

    val injector = document.create.inject(this, listOf(
            InjectByClassName("blotter-instrument-name") to ::nameSpan,
            InjectByClassName("blotter-instrument-volume-buy") to ::buyVolumeSpan,
            InjectByClassName("blotter-instrument-volume-sell") to ::sellVolumeSpan,
            InjectByClassName("blotter-slot-buy") to ::blotterSlotBuy,
            InjectByClassName("blotter-slot-sell") to ::blotterSlotSell
    ))

    val root = injector.div {
        classes = setOf("panel", "panel-default")

        div {
            classes = setOf("panel-heading")

            button {
                classes = setOf("btn btn-primary btn-small pull-right")
                onClickFunction = {
                    presenter.onPlaceOrderClicked()
                }
                +"Place order"
            }

            h3 {
                classes = setOf("panel-title")

                +"Blotter for instrument "
                span {
                    classes = setOf("blotter-instrument-name")
                }
            }

            p {
                +"Volume "
                +"Buy: "
                span { classes = setOf("blotter-instrument-volume-buy") }
                +" Sell: "
                span { classes = setOf("blotter-instrument-volume-sell") }
            }
        }

        div {
            classes = setOf("blotter-slot", "blotter-slot-buy")
            style = "display: inline-block; width: 400px; height: 600px; margin: 10px"
        }
        div {
            classes = setOf("blotter-slot", "blotter-slot-sell")
            style = "display: inline-block; width: 400px; height: 600px; margin: 10px"
        }
    }

    override var instrumentTitle: String
        get() = throw UnsupportedOperationException()
        set(value) {
            nameSpan.textContent = value
        }

    override var buyVolume: Double
        get() = throw UnsupportedOperationException()
        set(value) {
            buyVolumeSpan.textContent = value.toString()
        }

    override var sellVolume: Double
        get() = throw UnsupportedOperationException()
        set(value) {
            sellVolumeSpan.textContent = value.toString()
        }

    override fun createBlotterViews(): Map<OrderDirection, InstrumentBlotterView> = mapOf(
            OrderDirection.BUY to InstrumentBlotterViewImpl().let { blotterSlotBuy.appendChild(it.root); it },
            OrderDirection.SELL to InstrumentBlotterViewImpl().let { blotterSlotSell.appendChild(it.root); it }
    )

    override fun createPlaceOrderDialog(): PlaceOrderDialogView = PlaceOrderDialogViewImpl()
}