package cg.test

import cg.test.bootstrap.containerFluid
import html4k.stream.appendHTML
import html4k.*
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

WebServlet(name = "myServlet", urlPatterns = array("/servlet"))
class MyServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setContentType("text/html")
        resp.setCharacterEncoding("UTF-8")

        resp.getWriter().appendln("<!DOCTYPE html>")
        resp.getWriter().appendHTML().html {
            head {
                title("Server-side example")
                styleLink("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css")
                styleLink("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css")
            }
            body {
                containerFluid {
                    h1 {+"Server side example"}
                    p { +"This is just example to demonstrate ability to generate HTML at server side" }
                    p { +"Navigate to "
                        a("index.html") { +"main page" }
                        br
                        +" or simply press "
                        kbd {
                            kbd { +"Alt" }
                            +" + "
                            kbd { +"←" }
                        }
                    }
                }
            }
        }.flush()
    }
}
