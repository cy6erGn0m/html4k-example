package cg.test

import html4k.TagConsumer
import html4k.consumers.onFinalize
import html4k.dom.JSDOMBuilder
import org.w3c.dom.Node
import java.util.ArrayList
import kotlin.js.dom.html.HTMLDocument
import kotlin.js.dom.html.HTMLElement

public fun HTMLDocument.createTree0(): TagConsumer<HTMLElement> = JSDOMBuilder(this)
public fun Node.append0(block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> =
        ArrayList<HTMLElement>().let { result ->
            (ownerDocument as HTMLDocument).createTree0().onFinalize { result.add(it); appendChild(it) }.block()

            result
        }