package me.baggi.bdebug.exception

class PageNotFoundException(id: String) : RuntimeException(
    "Page by id($id) not found"
)