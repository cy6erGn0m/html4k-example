package cg.test

import html4k.*
import html4k.consumers.onFinalize
import html4k.dom.*
import html4k.js.*
import org.w3c.dom.Node
import java.util.ArrayList
import kotlin.js.dom.html.*

// workaround
public fun HTMLDocument.createTree0() : TagConsumer<HTMLElement> = JSDOMBuilder(this)
public fun Node.append0(block : TagConsumer<HTMLElement>.() -> Unit) : List<HTMLElement> =
        ArrayList<HTMLElement>().let { result ->
            (ownerDocument as HTMLDocument).createTree0().onFinalize { result.add(it); appendChild(it) }.block()

            result
        }
// workaround end

fun onReady() {
    document.body.append0 {
        this.nav {
            classes = "navbar navbar-default navbar-static-top".split(" ").toSet()
            div(setOf("container")) {
                div(setOf("navbar-header")) {
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

                    a("#", null) {
                        classes = setOf("navbar-brand")
                        +"Example"
                    }
                }
                div(setOf("navbar-collapse", "collapse")) {
                    id = "navbar"

                    ul {
                        classes = setOf("nav", "navbar-nav")

                        li {
                            classes = setOf("active")
                            a("#") { +"Home" }
                        }

                        li { a("#about") { +"About" } }
                        li { a("#contact") { +"Contact" } }

                        li {
                            classes = setOf("dropdown")

                            a("#") {
                                classes = setOf("dropdown-toggle")
                                attributes["data-toggle"] = "dropdown"
                                role = "button"
                                attributes["aria-expanded"] = "false"
                                +"Dropdown"
                                span {
                                    classes = setOf("caret")
                                }
                            }

                            ul {
                                classes = setOf("dropdown-menu")
                                role = "menu"

                                li { a("#") { +"Action" } }
                                li { a("#") { +"Another action" } }
                                li { a("#") { +"Something else here" } }
                                li { classes = setOf("divider") }
                                li { classes = setOf("dropdown-header"); +"Nav header" }
                                li { a("#") { +"Separated link" } }
                                li { a("#") { +"One more separated link" } }
                            }
                        }
                    }
                    ul {
                        classes = setOf("nav navbar-nav navbar-right")

                        li { a("http://getbootstrap.com/examples/navbar/") {+"Original bootstrap page"} }
                        li { classes = setOf("active"); a("http://getbootstrap.com/examples/navbar/") {+"Original bootstrap page"} }
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

                p { +"To see the difference between static and fixed top navbars, just scroll."}

                p {
                    a("http://getbootstrap.com/components/#navbar") {
                        classes = setOf("btn btn-lg btn-primary")
                        role = "button"

                        +"View navbar docs »"
                    }
                }
            }
        }

        Unit // do not remove me
    }
}

fun main(args: Array<String>) {
    window.onload = ::onReady
}