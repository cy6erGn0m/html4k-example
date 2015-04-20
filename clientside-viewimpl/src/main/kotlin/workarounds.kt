package market.web.impl

import html4k.DIV
import html4k.TR
import html4k.TagConsumer
import html4k.consumers.onFinalize
import html4k.dom.JSDOMBuilder
import html4k.injector.InjectCapture
import html4k.injector.InjectorConsumer
import html4k.visit
import org.w3c.dom.Node
import java.util.ArrayList
import kotlin.Pair
import kotlin.js.dom.html.HTMLCollection
import kotlin.js.dom.html.HTMLDivElement
import kotlin.js.dom.html.HTMLDocument
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLTableRowElement
import kotlin.reflect.KMutableMemberProperty

native fun HTMLElement.getElementsByClassName(classes: String): HTMLCollection
inline fun HTMLCollection.forEach(block: (Node) -> Unit) {
    for (i in 0..length) {
        block(item(i)!!)
    }
}

inline fun HTMLCollection.forEachElement(block: (HTMLElement) -> Unit) {
    for (i in 0..length) {
        val node = item(i)
        if (node is HTMLElement) {
            block(node)
        }
    }
}
