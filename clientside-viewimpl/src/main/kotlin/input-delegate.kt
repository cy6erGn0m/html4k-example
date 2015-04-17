package market.web.impl

import html4k.minus
import java.util.*
import kotlin.js.dom.html.HTMLInputElement
import kotlin.properties.ReadWriteProperty

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
        val classes = field.classesSet
        val changed = if (value) classes.remove(invalidClassName) else classes.add(invalidClassName)

        if (changed) {
            field.className = classes.join(" ")
        }
    }

    private val HTMLInputElement.classesSet : HashSet<String>
        get() = field.className.split("\\s+").toHashSet()
}