package market.web.impl

import html4k.minus
import org.w3c.dom.Node
import java.util.*
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLInputElement
import kotlin.properties.ReadWriteProperty

class NodeTextContentDelegate(val node : Node) : ReadWriteProperty<Any, String> {
    override fun get(thisRef: Any, desc: PropertyMetadata): String = node.textContent

    override fun set(thisRef: Any, desc: PropertyMetadata, value: String) {
        node.textContent = value
    }
}

class InputFieldDelegate(val field : HTMLInputElement) : ReadWriteProperty<Any, String> {
    override fun get(thisRef: Any, desc: PropertyMetadata): String = field.value

    override fun set(thisRef: Any, desc: PropertyMetadata, value: String) {
        field.value = value
    }
}

class InputValidDelegate(val field : HTMLInputElement, val invalidClassName : String = "alert-danger") : ReadWriteProperty<Any, Boolean> {
    override fun get(thisRef: Any, desc: PropertyMetadata): Boolean =
        invalidClassName in field.classesSet

    override fun set(thisRef: Any, desc: PropertyMetadata, value: Boolean) {
        field.classIf(invalidClassName, !value)
    }
}

fun HTMLElement.classIf(className : String, condition : Boolean) {
    val classes = classesSet.toHashSet()

    val changed = if (condition) classes.add(className) else classes.remove(className)

    if (changed) {
        this.className = classes.join(" ")
    }
}

fun HTMLElement.attributeIf(attributeName : String, attributeValue : String, condition : Boolean) {
    if (condition) {
        setAttribute(attributeName, attributeValue)
    } else {
        removeAttribute(attributeName)
    }
}

val HTMLElement.classesSet : Set<String>
    get() = this.className.split("\\s+").toSet()