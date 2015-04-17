package cg.test.bootstrap

import html4k.*

val CoreAttributeGroupFacade.active : Unit
    get() {
        classes = setOf("active")
    }

fun FlowContent.container(block : DIV.() -> Unit) : Unit = div(setOf("container"), block)
fun FlowContent.containerFluid(block : DIV.() -> Unit) : Unit = div(setOf("container-fluid"), block)



// navbar


fun FlowContent.navbarBrand(href : String = "#", block : A.() -> Unit) : Unit = a(href, null) {
    classes = setOf("navbar-brand")
    block()
}
fun FlowContent.navbarHeader(block : DIV.() -> Unit) : Unit = div(setOf("navbar-header"), block)
fun FlowContent.navbarCollapse(block : DIV.() -> Unit) : Unit = div(setOf("navbar-collapse", "collapse")) {id = "navbar"; block()}
fun FlowContent.navbarNav(block : UL.() -> Unit) : Unit = ul {
    classes = setOf("nav", "navbar-nav")
    block()
}
fun FlowContent.navbarRight(block : UL.() -> Unit) : Unit = ul {
    classes = setOf("nav navbar-nav navbar-right")
    block()
}


// dropdown
fun UL.dropdown(block : LI.() -> Unit) {
    li {
        classes = setOf("dropdown")

        block()
    }
}

fun LI.dropdownToggle(block : A.() -> Unit) {
    a("#", null) {
        classes = setOf("dropdown-toggle")
        attributes["data-toggle"] = "dropdown"
        role = "button"
        attributes["aria-expanded"] = "false"

        block()

        span {
            classes = setOf("caret")
        }
    }

}

fun LI.dropdownMenu(block : UL.() -> Unit) : Unit = ul {
    classes = setOf("dropdown-menu")
    role = "menu"

    block()
}

fun UL.dropdownHeader(text : String) : Unit = li { classes = setOf("dropdown-header"); +text }
fun UL.divider() : Unit = li { classes = setOf("divider")}


// forms

fun FORM.formGroup(block : DIV.() -> Unit) = div(setOf("form-group"), block)
private fun FlowContent.buttonImpl(_classes : Set<String>, block : BUTTON.() -> Unit) = button(type = ButtonType.button) {
    classes = _classes

    block()
}
fun FlowContent.buttonPrimary(block : BUTTON.() -> Unit) = buttonImpl(setOf("btn", "btn-primary"), block)
fun FlowContent.buttonDefault(block : BUTTON.() -> Unit) = buttonImpl(setOf("btn", "btn-default"), block)

fun FlowContent.icon(name : String) = i {
    classes = setOf("glyphicon", "glyphicon-$name")
}

fun DIV.spinner(block : INPUT.() -> Unit) = div {
    classes = setOf("input-group", "spinner")

    textInput {
        classes = setOf("form-control")

        block()
    }

    div {
        classes = setOf("input-group-btn-vertical")

        buttonDefault {
            classes += "spinner-up"
            icon("chevron-up")
        }
        buttonDefault {
            classes += "spinner-down"

            icon("chevron-down")
        }
    }
}