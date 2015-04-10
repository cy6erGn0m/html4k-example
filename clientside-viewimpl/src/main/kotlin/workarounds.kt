package cg.test.view.impl

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

public fun HTMLDocument.createTree0(): TagConsumer<HTMLElement> = JSDOMBuilder(this)
public fun HTMLElement.createTree0(): TagConsumer<HTMLElement> = JSDOMBuilder(ownerDocument as HTMLDocument)
public fun Node.append0(block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> =
        ArrayList<HTMLElement>().let { result ->
            (ownerDocument as HTMLDocument).createTree0().onFinalize { it, partial -> if (!partial) {result.add(it); appendChild(it)} }.block()

            result
        }

public fun Node.appendWith0(
        chain: (TagConsumer<HTMLElement>) -> TagConsumer<HTMLElement>,
        block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> = append0 { chain(this).block() }

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

public fun <T> TagConsumer<HTMLElement>.inject0(bean: T, rules: List<Pair<InjectCapture, KMutableMemberProperty<T, out HTMLElement>>>): TagConsumer<HTMLElement> = InjectorConsumer(this, bean, rules)

public fun TagConsumer<HTMLElement>.tr0(block: html4k.TR.() -> Unit): HTMLTableRowElement = with(TR(emptyMap(), this)) { visit(block); this@tr0.finalize() as HTMLTableRowElement }
public fun TagConsumer<HTMLElement>.div0(block: html4k.DIV.() -> Unit): HTMLDivElement = with(DIV(emptyMap(), this)) { visit(block); this@div0.finalize() as HTMLDivElement }