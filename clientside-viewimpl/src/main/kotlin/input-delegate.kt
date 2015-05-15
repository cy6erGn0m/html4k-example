package market.web.impl

import html4k.minus
import org.w3c.dom.Node
import java.util.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.dom.hasClass
import kotlin.properties.ReadWriteProperty

class NodeTextContentDelegate(val node : Node) : ReadWriteProperty<Any, String> {
    override fun get(thisRef: Any, desc: PropertyMetadata): String = node.textContent ?: ""

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
        field.hasClass(invalidClassName)

    override fun set(thisRef: Any, desc: PropertyMetadata, value: Boolean) {
        field.classIf(invalidClassName, !value)
    }
}

fun HTMLElement.classIf(className : String, condition : Boolean) {
    if (condition) {
        classList.add(className)
    } else {
        classList.remove(className)
    }
}

fun HTMLElement.attributeIf(attributeName : String, attributeValue : String, condition : Boolean) {
    if (condition) {
        setAttribute(attributeName, attributeValue)
    } else {
        removeAttribute(attributeName)
    }
}
