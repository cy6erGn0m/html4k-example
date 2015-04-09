package cg.test

import html4k.*
import html4k.TagConsumer
import html4k.consumers.onFinalize
import html4k.dom.JSDOMBuilder
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.ArrayList
import kotlin.js.dom.html.*

public fun HTMLDocument.createTree0(): TagConsumer<HTMLElement> = JSDOMBuilder(this)
public fun HTMLElement.createTree0(): TagConsumer<HTMLElement> = JSDOMBuilder(ownerDocument as HTMLDocument)
public fun Node.append0(block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> =
        ArrayList<HTMLElement>().let { result ->
            (ownerDocument as HTMLDocument).createTree0().onFinalize { result.add(it); appendChild(it) }.block()

            result
        }

native fun HTMLElement.getElementsByClassName(classes : String) : HTMLCollection
inline fun HTMLCollection.forEach(block : (Node) -> Unit) {
    for (i in 0..length) {
        block(item(i)!!)
    }
}
inline fun HTMLCollection.forEachElement(block : (HTMLElement) -> Unit) {
    for (i in 0..length) {
        val node = item(i)
        if (node is HTMLElement) {
            block(node)
        }
    }
}

public fun TagConsumer<HTMLElement>.tr0(block : html4k.TR.() -> Unit) : HTMLTableRowElement = with(TR(emptyMap(), this)) {visit(block); this@tr0.finalize() as HTMLTableRowElement}
public fun TagConsumer<HTMLElement>.div0(block : html4k.DIV.() -> Unit) : HTMLDivElement = with(DIV(emptyMap(), this)) {visit(block); this@div0.finalize() as HTMLDivElement}