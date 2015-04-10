package cg.test

trait QuoteRowViewModel {
    fun setInstrumentName(name: String)
    fun setValue(value: Double)
    fun setMove(move: QuoteMove)
}

trait QuotesTableViewModel {
    fun start() : Unit
    fun appendRowModel(row : QuoteRowViewModel)
    fun createRowModel() : QuoteRowViewModel
}

trait MainView {
    fun createQuotesTable() : QuotesTableViewModel
}