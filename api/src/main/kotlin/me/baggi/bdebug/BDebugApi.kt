package me.baggi.bdebug

interface BDebugApi {
    fun addOrChangePage(id: String, lines: List<String>)

    fun removePage(id: String)
}