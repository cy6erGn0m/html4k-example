package market.web.impl

import html4k.injector.*
import market.view.mainpage.mainPageContent
import market.web.MainPresenter
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.asList
import kotlin.dom.clear
import kotlin.dom.toElementList

fun onReady() {
    val mainView = MainViewModel()

    val existingQuotesTableSlot = document.body!!.querySelectorAll(".quotes-slot").toElementList().firstOrNull() as HTMLElement?
    val existingInstrumentViewSlot = document.body!!.querySelectorAll(".instrument-view-slot").toElementList().firstOrNull() as HTMLElement?

    if (existingQuotesTableSlot != null && existingInstrumentViewSlot != null) {
        mainView.quotesTableSlot = existingQuotesTableSlot
        mainView.instrumentViewSlot = existingInstrumentViewSlot
    } else {
        document.body!!.clear()
        document.body!!.appendAndInject(mainView,
                listOf(
                        InjectByClassName("quotes-slot") to MainViewModel::quotesTableSlot,
                        InjectByClassName("instrument-view-slot") to MainViewModel::instrumentViewSlot
                )
        ) {
            mainPageContent()
        }
    }

    val presenter = MainPresenter(mainView, WebSocketServiceImpl { listener ->
        KWebSocketImpl("ws://${window.location.host}/${window.location.pathname.removePrefix("/").removeSuffix("/")}/ws", 3000, listener)
    })
    presenter.start()
}

fun main(args: Array<String>) {
    window.onload = { onReady() }
}