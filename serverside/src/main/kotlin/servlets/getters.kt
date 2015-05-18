package market.servlets

import com.google.gson.stream.JsonWriter
import market.*
import market.model.*
import market.model.server.*
import market.events.*
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.concurrent.withLock

val m = Market()

WebServlet(urlPatterns = arrayOf("/blotter"), name = "BlotterServlet", loadOnStartup = 1)
class ListOrdersServlet : HttpServlet() {

    private val lock = ReentrantLock()
    private val orders = TreeSet<OrderOnStack>()

    override fun init() {
        super<HttpServlet>.init()

        m.ordersOnStack.forEach {
            lock.withLock {
                orders.add(it.orderOnStack)
            }
        }
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val orders = lock.withLock { orders.toList() }

        resp.setContentType("application/json;charset=UTF-8")

        with(JsonWriter(resp.getWriter())) {
            beginArray()

            orders.forEach {
                beginObject()

                name("i")
                value(it.instrument)

                name("qty")
                value(it.quantity)

                name("p")
                value(it.order.price.toPlainString())

                name("bs")
                value(it.order.direction.toString())

                endObject()
            }

            endArray()
            flush()
        }
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val instrument = req.getParameter("i")!!
        val price = BigDecimal(req.getParameter("p"))
        val quantity = req.getParameter("q").toInt()
        val buySell = OrderDirection.valueOf(req.getParameter("bs"))

        m.orderObserver.onNext(ItemPlaced<Order>(SimpleOrder(instrument, price, quantity, buySell)))

        resp.setContentType("text/plain")
        resp.getWriter().println("OK")
        resp.getWriter().flush()
    }
}
