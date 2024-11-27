package me.baggi.bdebug

interface BDebugApi {
    fun addPage(id: String, pageData: PageData)

    fun changeLines(pageId: String, list: List<String>)

    fun changeLines(pageId: String, vararg lines: String)

    fun removePage(id: String)
}