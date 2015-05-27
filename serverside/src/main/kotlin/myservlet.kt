package cg.test

import html4k.stream.appendHTML
import market.server.mainpage.mainPageTemplate
import market.view.mainpage.mainPageContent
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

WebServlet(name = "myServlet", urlPatterns = arrayOf("/servlet"))
class MyServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setContentType("text/html")
        resp.setCharacterEncoding("UTF-8")

        resp.getWriter().appendln("<!DOCTYPE html>")
        resp.getWriter().appendHTML().mainPageTemplate {
            mainPageContent()
        }.flush()
    }
}

WebServlet(name = "index.html", urlPatterns = arrayOf("/index.html"))
class IndexPageServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setContentType("text/html")
        resp.setCharacterEncoding("UTF-8")

        resp.getWriter().appendln("<!DOCTYPE html>")
        resp.getWriter().appendHTML().mainPageTemplate {
        }.flush()
    }
}
