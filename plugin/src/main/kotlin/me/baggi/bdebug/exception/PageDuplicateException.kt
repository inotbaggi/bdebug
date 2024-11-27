package me.baggi.bdebug.exception

class PageDuplicateException(id: String) : RuntimeException(
    "Page with id($id) already exists!"
)