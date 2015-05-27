package market.view.mainpage

import html4k.*
import market.web.impl.bootstrap.*

fun <T> TagConsumer<T>.mainPageContent() {
    nav {
        classes = setOf("navbar", "navbar-default", "navbar-static-top")
        container {
            navbarHeader {
                button {
                    type = ButtonType.button
                    classes = setOf("navbar-toggle", "collapsed")
                    attributes["data-toggle"] = "collapse"
                    attributes["data-target"] = "#navbar"
                    attributes["aria-expanded"] = "false"
                    attributes["aria-controls"] = "navbar"

                    span { classes = setOf("sr-only"); +"Toggle navigation" }
                    span { classes = setOf("icon-bar") }
                    span { classes = setOf("icon-bar") }
                    span { classes = setOf("icon-bar") }
                }

                navbarBrand {
                    +"Example"
                }
            }
            navbarCollapse {
                navbarNav {
                    li {
                        active
                        a("#") { +"Home" }
                    }
                    li { a("#about") { +"About" } }
                    li { a("#contact") { +"Contact" } }

                    dropdown {
                        dropdownToggle { +"Dropdown" }
                        dropdownMenu {
                            li { a("#") { +"Action" } }
                            li { a("#") { +"Another action" } }
                            li { a("#") { +"Something else here" } }
                            divider()
                            dropdownHeader("Nav header")
                            li { a("#") { +"Separated link" } }
                            li { a("#") { +"One more separated link" } }
                        }
                    }
                }
                navbarRight {
                    li { a("http://getbootstrap.com/examples/navbar/") {+"Original bootstrap page"} }
                    li { active; a("http://getbootstrap.com/examples/navbar/") {+"Original bootstrap page"} }
                    li { a("http://getbootstrap.com/examples/navbar/") {+"Original bootstrap page"} }
                }
            } // navbar-collapse
        }
    }
    div {
        classes = setOf("container")

        div {
            classes = setOf("jumbotron")

            h1 { +"Navbar example" }
            p {+"""This example is a quick exercise to illustrate how the
                    default, static and fixed to top navbar work. It includes the responsive
                    CSS and HTML, so it also adapts to your viewport and device."""}

            p {
                +"You also can generate HTML at server side "
                br()
                +"see "
                a("servlet") {+"servlet-generated page"}
            }

            p {
                a("http://getbootstrap.com/components/#navbar") {
                    classes = setOf("btn btn-lg btn-primary")
                    role = "button"

                    +"View navbar docs »"
                }
            }
        }

        div {
            classes = setOf("quotes-slot")
        }

        div {
            classes = setOf("instrument-view-slot")
        }
    }
}