package cg.test.view.impl

import cg.test.QuotesListener
import kotlin.js.dom.html.window

fun randomInterval() = Math.max(50.0, Math.abs(Math.random() - 0.5) * 1000.0)

fun startTicker(listener : QuotesListener, interval : Double = randomInterval()) {
    window.setTimeout({
        val id = (Math.random() * 9).toInt()
        val instrument = "INSTR-$id"
        val quote = Math.random() * 10.0 + id * 100.0

        listener.onQuote(instrument, quote)

        startTicker(listener)
    }, interval)
}