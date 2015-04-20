package market.web.impl

import html4k.*
import html4k.js.*
import html4k.dom.*
import html4k.injector.*
import cg.test.PlaceOrderDialogView
import cg.test.bootstrap.*
import cg.test.view.impl.removeFromParent
import jquery.JQuery
import jquery.jq
import market.model.OrderDirection
import market.web.PlaceOrderDialogPresenter
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import kotlin.dom.toList
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLInputElement
import kotlin.js.dom.html.document
import kotlin.js.dom.html.window
import kotlin.properties.Delegates

class PlaceOrderDialogViewImpl : PlaceOrderDialogView {
    override var presenter : PlaceOrderDialogPresenter by Delegates.notNull()

    private var nameSpan : HTMLElement by Delegates.notNull()
    private var placeButton : HTMLElement by Delegates.notNull()
    private var priceText : HTMLInputElement by Delegates.notNull()
    private var quantityText : HTMLInputElement by Delegates.notNull()
    private var quantityUp: HTMLElement by Delegates.notNull()
    private var quantityDown: HTMLElement by Delegates.notNull()
    private var buyRadioButton : HTMLElement by Delegates.notNull()
    private var sellRadioButton : HTMLElement by Delegates.notNull()

    private val injector = document.create.inject(this, listOf(
            InjectByClassName("instrument-name") to ::nameSpan,
            InjectByClassName("btn-primary") to ::placeButton,
            InjectByClassName("price") to ::priceText,
            InjectByClassName("quantity") to ::quantityText,
            InjectByClassName("spinner-up") to ::quantityUp,
            InjectByClassName("spinner-down") to ::quantityDown,
            InjectByClassName("buyRadioButton") to ::buyRadioButton,
            InjectByClassName("sellRadioButton") to ::sellRadioButton
    ))

    val root = injector.div {
        id = "placeOrderDialog"
        classes = setOf("modal", "fade")
        role = "dialog"

        attributes["aria-labelledby"] = "myModalLabel"
        attributes["aria-hidden"] = "true"

        div {
            classes = setOf("modal-dialog")

            div {
                classes = setOf("modal-content")

                div {
                    classes = setOf("modal-header")

                    button(type = ButtonType.button) {
                        classes = setOf("close")

                        attributes["data-dismiss"] = "modal"
                        attributes["aria-label"] = "Cancel"

                        span {
                            attributes["aria-hidden"] = "true"
                            +Entities.times
                        }
                    }

                    h4 {
                        classes = setOf("modal-title")
                        id = "myModalLabel"

                        +"Place new order for "
                        span {
                            classes = setOf("instrument-name")
                        }
                    }
                }

                div {
                    classes = setOf("modal-body")

                    form {
                        formGroup {
                            label {
                                +"Buy or sell"
                            }
                            radioGroup("buySell") {
                                radioButton(RadioButtonType.radioButton) {
                                    classes += "buyRadioButton"
                                    +"Buy"
                                }
                                radioButton(RadioButtonType.radioButton) {
                                    classes += "sellRadioButton"
                                    +"Sell"
                                }
                            }
                        }
                        formGroup {
                            label {
                                +"Price"
                            }
                            textInput {
                                classes = setOf("price", "form-control")
                                placeholder = "Enter price"
                            }
                        }
                        formGroup {
                            label {
                                +"Quantity"
                            }
                            spinner {
                                classes += "quantity"
                            }
                        }
                    }
                }

                div {
                    classes = setOf("modal-footer")

                    buttonDefault {
                        attributes["data-dismiss"] = "modal"

                        +"Cancel"
                    }

                    buttonPrimary {
                        +"Save"
                    }
                }
            }
        }
    }

    init {
        quantityUp.onclick = { presenter.onQuantityUpClicked() }
        quantityDown.onclick = { presenter.onQuantityDownClicked() }
        placeButton.onclick = { presenter.onAccepted() }

        priceText.onkeydown = { defer { presenter.doValidate() } }
        priceText.onkeyup = { defer { presenter.doValidate() } }
        quantityText.onkeydown = { defer { presenter.doValidate() } }
        quantityText.onkeyup = { defer { presenter.doValidate() } }

        buyRadioButton.onclick = { presenter.onBuyButtonClicked() }
        sellRadioButton.onclick = { presenter.onSellButtonClicked() }

        jq(priceText).change { defer { presenter.doValidate() } }
        jq(quantityText).change { defer { presenter.doValidate() } }
    }

    override var instrumentName: String by NodeTextContentDelegate(nameSpan)

    override var price: String by InputFieldDelegate(priceText)

    override var buyActive : Boolean
        get() = throw UnsupportedOperationException()
        set(active) {
            setRadioButtonActive(buyRadioButton, active)
        }

    override var sellActive : Boolean
        get() = throw UnsupportedOperationException()
        set(active) {
            setRadioButtonActive(sellRadioButton, active)
        }

    private fun setRadioButtonActive(button : HTMLElement, active : Boolean) {
        button.classIf("active", active)
        button.getElementsByTagName("input").toList().filter {it.attributes.getNamedItem("type").value == "radio"}.forEach {
            it.asElement.attributeIf("checked", "checked", active)
        }
    }

    override var quantity: String by InputFieldDelegate(quantityText)

    override fun show() {
        document.body.appendChild(root)
        jq(root).modal("show")
    }

    override fun hide() {
        jq(root).modal("hide")
        window.setTimeout({
            root.removeFromParent()
        }, 300)
    }

    override var priceValid: Boolean by InputValidDelegate(priceText)

    override var quantityValid: Boolean by InputValidDelegate(quantityText)

    override fun showTooltip(text: String) {
    }

    override fun hideTooltip() {
    }
}

private native fun JQuery.modal(showHide : Any)
private fun defer(block : () -> Unit) = window.setTimeout(block, 0)
private val Node.asElement : HTMLElement
    get() = if (this is HTMLElement) this else throw IllegalArgumentException()
private native val Node.value : String