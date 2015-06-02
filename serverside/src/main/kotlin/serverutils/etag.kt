package serverutils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


fun Pair<HttpServletRequest, HttpServletResponse>.withEtag(vararg keys: Any?, block: (HttpServletRequest, HttpServletResponse) -> Unit) {
    val (request, response) = this

    val givenNoneMatchEtags = request.getHeaders("If-None-Match").toList().flatMap(String::parseMatchTag).toSet()
    val givenMatchEtags = request.getHeaders("If-Match").toList().flatMap(String::parseMatchTag).toSet()
    val currentEtag = keys.toList().toEtag()

    if (currentEtag in givenNoneMatchEtags && "*" !in givenNoneMatchEtags) {
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED)
        return
    }
    if (givenMatchEtags.isNotEmpty() && currentEtag !in givenMatchEtags && "*" !in givenMatchEtags) {
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED, "Got etag ${currentEtag}")
        return
    }

    response.setHeader("ETag", currentEtag)
    block(request, response)
}

private fun String.parseMatchTag() = split("\\s*,\\s*".toRegex()).map { it.removePrefix("W/") }.filter { it.isNotEmpty() }

fun <T> Iterable<T>.toEtag(): String = map(Any::toEtagImpl).joinToString("/", "//", "\\")
private fun Any?.toEtagImpl(): String = when (this) {
    null -> "0"
    is String -> this
    is CharSequence -> this.toString()
    is Iterable<*> -> this.toEtag()
    is Array<*> -> this.toList().toEtag()
    else -> this.toString()
}
