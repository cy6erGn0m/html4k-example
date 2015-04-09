package cg.test

import kotlin.js.dom.html.window

fun randomInterval() = Math.max(50.0, Math.random() * 2000)

fun startTicker(listener : QuotesListener, interval : Double = randomInterval()) {
    window.setTimeout({
        val id = (Math.random() * 9).toInt()
        val instrument = "INSTR-$id"
        val quote = Math.random() * 10.0 + id * 100.0

        listener.onQuote(instrument, quote)

        startTicker(listener)
    }, interval)
}