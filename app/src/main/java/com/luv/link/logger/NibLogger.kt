package com.luv.link.logger

interface NibLogger {
    fun logsIt(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
    }

    fun debug(
        tag: String?,
        msg: String
    ) {
    }

    fun error(
        tag: String? = null,
        msg: String = "",
        t: Throwable? = null
    ) {
    }
}
