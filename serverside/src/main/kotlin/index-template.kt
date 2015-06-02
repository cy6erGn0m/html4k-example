package market.server.mainpage

import html4k.*

val knownScripts = listOf("js/kotlin.js",
        "js/kotlinx.html.shared.js",
        "js/kotlinx.html.js.js",
        "js/shared.js",
        "js/clientside.js")

fun <T> TagConsumer<T>.mainPageTemplate(content: TagConsumer<T>.() -> Unit): T =
    html {
        head {
            title("Server-side example")
            meta {
                charset = "UTF-8"
            }

            script(ScriptType.textJavaScript, "//code.jquery.com/jquery-1.11.2.min.js") {}
            script(ScriptType.textJavaScript, "//code.jquery.com/jquery-migrate-1.2.1.min.js") {}
            script(ScriptType.textJavaScript, "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js") {}

            for (script in knownScripts) {
                script(ScriptType.textJavaScript, script) {}
            }

            styleLink("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css")
            styleLink("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css")

            styleLink("main.css")
        }
        body {
            content()
        }
    }
