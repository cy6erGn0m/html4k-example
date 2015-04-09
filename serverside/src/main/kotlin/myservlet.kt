package cg.test

import html4k.stream.appendHTML
import html4k.*
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

WebServlet("myServlet", urlPatterns = array("/servlet"))
class MyServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setContentType("text/html")
        resp.setCharacterEncoding("UTF-8")

        resp.getWriter().appendHTML().html {
            body {
                head {
                    title("Server-side example")
                    link {
                        rel = LinkRel.stylesheet
                        href = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css"
                    }
                    link {
                        rel = LinkRel.stylesheet
                        href = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css"
                    }
                }
                body {
                    div {
                        classes = setOf("container-fluid")

                        h1 {+"Server side example"}
                        p { +"This is just example to demonstrate ability to generate HTML at server side" }
                        p { +"Navigate to "
                            a("index.html") { +"main page" }
                            +" or simply press "
                            kbd {
                                kbd { +"Alt" }
                                +" + "
                                kbd { +"←" }
                            }
                        }
                    }
                }
            }
        }.flush()
    }
}

/*
<!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
 */