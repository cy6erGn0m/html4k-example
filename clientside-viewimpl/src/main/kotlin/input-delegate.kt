package market.web.impl

import kotlin.js.dom.html.HTMLInputElement
import kotlin.properties.ReadWriteProperty

class InputFieldDelegate(val field : HTMLInputElement) : ReadWriteProperty<Any, String> {
    override fun get(thisRef: Any, desc: PropertyMetadata): String = field.value

    override fun set(thisRef: Any, desc: PropertyMetadata, value: String) {
        field.value = value
    }
}