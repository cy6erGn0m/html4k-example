package tests

import kotlin.test.assertEquals
import org.junit.Test as test

class MainTest {
    test fun f1() {
        println("OK")
        assertEquals("OK", "OK")
    }

//    test fun f2() {
//        throw IllegalArgumentException()
//    }
}