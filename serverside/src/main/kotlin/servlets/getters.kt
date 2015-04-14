package market.servlets

import com.google.gson.stream.JsonWriter
import market.*
import market.events.*
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.concurrent.withLock

val m = Market(object: TradeListener {
    override fun onTrade(trade: OrderTrade) {
        println("trade")
    }
})

WebServlet(urlPatterns = array("/blotter"), name = "BlotterServlet", loadOnStartup = 1)
class ListOrdersServlet : HttpServlet(), ItemEventListener<OrderOnStack> {

    private val lock = ReentrantLock()
    private val orders = TreeSet<OrderOnStack>()

    override fun onEvent(event: ItemEvent<OrderOnStack>) {
        lock.withLock {
            orders.add(event.orderOnStack)
        }
    }

    override fun init() {
        super<HttpServlet>.init()

        EventListenersRegistry.register(this)
        EventListenersRegistry.register(m)
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

        EventListenersRegistry.send(ItemPlaced<Order>(SimpleOrder(instrument, price, quantity, buySell)))

        resp.setContentType("text/plain")
        resp.getWriter().println("OK")
        resp.getWriter().flush()
    }
}
