package cg.test.tests

import cg.test.*
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.ArrayList
import org.junit.Test as test

class QuotesTableTest {


    test fun moveUpDown() {
        val view = mock(javaClass<QuotesTableViewModel>())
        val createdRows = ArrayList<QuoteRowViewModel>()

        `when`(view.createRowModel()).thenAnswer {
            val row = mock(javaClass<QuoteRowViewModel>())
            createdRows.add(row)

            row
        }

        val presenter = QuotesTablePresenter(view)
        presenter.onQuote("A", 1.0)

        verify(view, times(1)).createRowModel()
        verify(view, times(1)).appendRowModel(any())
        verify(createdRows.single(), times(1)).setInstrumentName("A")
        verify(createdRows.single(), times(1)).setValue(1.0)
        verify(createdRows.single(), times(1)).setMove(QuoteMove.NEUTRAL)

        presenter.onQuote("A", 1.5)
        verify(createdRows.single(), times(1)).setInstrumentName("A")
        verify(createdRows.single(), times(1)).setValue(1.5)
        verify(createdRows.single(), times(1)).setMove(QuoteMove.UP)

        presenter.onQuote("A", 0.5)
        verify(createdRows.single(), times(1)).setInstrumentName("A")
        verify(createdRows.single(), times(1)).setValue(0.5)
        verify(createdRows.single(), times(1)).setMove(QuoteMove.DOWN)

        // verify totals
        verify(createdRows.single(), times(1)).setInstrumentName(any())
        verify(createdRows.single(), times(3)).setValue(Matchers.anyDouble())
        verify(createdRows.single(), times(3)).setMove(any())
    }

    suppress("NOTHING_TO_INLINE")
    private inline fun uninitialized<T>() = null as T

    private inline fun <reified T> any() : T {
        Matchers.any<T>()
        return uninitialized()
    }
}