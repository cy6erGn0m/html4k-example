package cg.test

import java.util.HashMap

class QuotesTablePresenter(val view: QuotesTableViewModel) : QuotesListener {
    private val instrumentRows = HashMap<String, QuoteRowViewModel>(128)
    private val lastQuotes = HashMap<String, Double>()

    override fun onQuote(instrument: String, value: Double) {
        val move = lastQuotes[instrument]?.let { previous ->
            if (value > previous) QuoteMove.UP
            else if (value < previous) QuoteMove.DOWN
            else QuoteMove.NEUTRAL
        } ?: QuoteMove.NEUTRAL

        lastQuotes[instrument] = value

        if (instrument !in instrumentRows) {
            val rowView = view.createRowModel()
            rowView.setInstrumentName(instrument)
            view.appendRowModel(rowView)

            instrumentRows[instrument] = rowView
        }

        val row = instrumentRows[instrument]!!
        row.setValue(value)
        row.setMove(move)
    }

}


