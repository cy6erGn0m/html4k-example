package cg.test.bootstrap

import html4k.*

val CoreAttributeGroupFacade.active : Unit
    get() {
        classes = setOf("active")
    }

fun HTMLTag.container(block : DIV.() -> Unit) : Unit = div(setOf("container"), block)
fun HTMLTag.containerFluid(block : DIV.() -> Unit) : Unit = div(setOf("container-fluid"), block)



// navbar


fun HTMLTag.navbarBrand(href : String = "#", block : A.() -> Unit) : Unit = a(href, null) {
    classes = setOf("navbar-brand")
    block()
}
fun HTMLTag.navbarHeader(block : DIV.() -> Unit) : Unit = div(setOf("navbar-header"), block)
fun HTMLTag.navbarCollapse(block : DIV.() -> Unit) : Unit = div(setOf("navbar-collapse", "collapse")) {id = "navbar"; block()}
fun HTMLTag.navbarNav(block : UL.() -> Unit) : Unit = ul {
    classes = setOf("nav", "navbar-nav")
    block()
}
fun HTMLTag.navbarRight(block : UL.() -> Unit) : Unit = ul {
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