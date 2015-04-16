package market.web.impl

import cg.test.KWebSocket
import market.web.impl
import java.util.*
import kotlin.js.dom.html.window
import kotlin.reflect.KMemberProperty

native
trait WebSocket {
    fun close(closeCode : Long, reason : String)
    fun close()
    fun send(text : String)

    var onopen : (() -> Unit)?
    var onmessage : ((String) -> Unit)?
    var onerror : (() -> Unit)?
    var onclose : (() -> Unit)?
}

native
fun WebSocket(url : String) : WebSocket

class KWebSocketImpl(val url : String, val listener : (dynamic) -> Unit) : KWebSocket {
    private var currentSocket : WebSocket? = null
    private val queue = LinkedList<String>()
    private var closed = false

    init {
        connect()
    }

    override fun stop() {
        closed = true
        try {
            currentSocket?.close()
        } catch (ignore : Throwable) {
            currentSocket = null
        }
    }

    override fun send(o: Any) {
        if (closed) {
            throw IllegalStateException("Socket already stopped")
        }

        val text = JSON.stringify(o)
        queue.add(text)

        flush()
    }

    private fun flush() {
        val s = currentSocket
        if (s == null) {
            return
        }

        try {
            val iterator = queue.iterator()
            while (iterator.hasNext()) {
                val text = iterator.next()

                s.send(text)

                iterator.remove()
            }
        } catch (ignore : Throwable) {
        }
    }

    private fun onMessage(message : dynamic) {
        listener(message)
    }

    private fun connect() {
        val socket = WebSocket(url)
        fun closeSocket() {
            try {
                currentSocket?.close()
                socket.close()
            } finally {
                currentSocket = null
            }
        }

        socket.onopen = {
            if (currentSocket != socket) {
                currentSocket?.close()
                currentSocket = socket
            }

            window.setTimeout({
                flush()
            }, 0)
        }

        socket.onerror = {
            closeSocket()
            connect()
        }

        socket.onmessage = {
            onMessage(JSON.parse(it))
            flush()
        }

        socket.onclose = {
            closeSocket()
        }
    }
}