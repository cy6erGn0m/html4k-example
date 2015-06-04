package market.web.impl

import market.web.KWebSocket
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import java.util.ArrayList
import kotlin.browser.window

class KWebSocketImpl(val url : String, val reconnectDelayMillis: Int, val listener : (dynamic) -> Unit) : KWebSocket {
    private var currentSocket : WebSocket? = null
    private val queue = ArrayList<String>()
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
        val s = currentSocket ?: return

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

    private fun reconnectWithDelay() {
        window.setTimeout({
            connect()
        }, reconnectDelayMillis)
    }

    private fun connect() {
        try {
            tryConnect()
        } catch (any : Throwable) {
            reconnectWithMessageAndDelay()
        }
    }

    private fun reconnectWithMessageAndDelay() {
        val secondsText = "^[0-9]+([,.][0-9]{1,2})?".toRegex()
                .match((reconnectDelayMillis / 1000.0).toString())!!
                .value.replace(".[0-9]+$".toRegex()) { it.value.replace("\\.?0+$".toRegex(), "") }

        console.error("WebSocket ($url) connection failure, will reconnect in ${secondsText}s")
        reconnectWithDelay()
    }

    private fun tryConnect() {
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
            if (currentSocket !== socket) {
                currentSocket?.close()
                currentSocket = socket
            }

            window.setTimeout({
                flush()
            }, 0)
        }

        socket.onerror = {
            closeSocket()
            reconnectWithMessageAndDelay()
        }

        socket.onmessage = {
            if (it is MessageEvent) {
                val data = it.data
                console.log("Received from websocket", data)
                if (data is String) {
                    onMessage(JSON.parse(data))
                }

                flush()
            }
        }

        socket.onclose = {
            if (socket === currentSocket) {
                currentSocket = null
            }

            if (!closed) {
                tryConnect()
            }
        }
    }
}
