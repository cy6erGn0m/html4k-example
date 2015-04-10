package cg.test.tests

import cg.test.*
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.junit.Test as test

class QuotesTableTest {


    test fun moveUp() {
        val view = mock(javaClass<QuotesTableViewModel>())
        `when`(view.createRowModel()).thenAnswer {
            mock(javaClass<QuoteRowViewModel>())
        }

        val presenter = QuotesTablePresenter(view)
        presenter.onQuote("A", 1.0)

        verify(view, times(1)).createRowModel()
        verify(view, times(1)).appendRowModel(any())
    }

    suppress("NOTHING_TO_INLINE")
    private inline fun uninitialized<T>() = null as T

    private inline fun <reified T> any() : T {
        Matchers.any<T>()
        return uninitialized()
    }
}