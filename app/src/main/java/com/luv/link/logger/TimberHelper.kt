package com.luv.link.logger

import android.util.Log
import javax.inject.Inject
import timber.log.Timber

class TimberHelper
@Inject
constructor() :
    Timber.Tree(),
    NibLogger {
    // Custom log levels can be added if needed
    enum class LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT
    }

    // Initialize Timber logging
    fun init() {
        // Add your custom Tree for Timber
        Timber.plant(
            object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    // Add custom tag, e.g., "MyApp" for all logs
                    return super.createStackElementTag(element) + " | MyApp"
                }
            }
        )
    }

    override fun debug(
        tag: String?,
        msg: String
    ) {
        Timber.v(msg, tag)
    }

    override fun logsIt(
        priority: Int,
        tag: String?,
        msg: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return // Skip verbose and debug logs
        }
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
    }

    // Logs a verbose message
    fun v(
        message: String,
        throwable: Throwable? = null
    ) {
        Timber.tag("VERBOSE").v(throwable, message)
    }

    // Logs a debug message
    fun d(
        message: String,
        throwable: Throwable? = null
    ) {
        Timber.tag("DEBUG").d(throwable, message)
    }

    // Logs an info message
    fun i(
        message: String,
        throwable: Throwable? = null
    ) {
        Timber.tag("INFO").i(throwable, message)
    }

    // Logs a warning message
    fun w(
        message: String,
        throwable: Throwable? = null
    ) {
        Timber.tag("WARN").w(throwable, message)
    }

    // Logs an error message
    fun e(
        message: String,
        throwable: Throwable? = null
    ) {
        Timber.tag("ERROR").e(throwable, message)
    }

    // Logs an assert message
    fun wtf(
        message: String,
        throwable: Throwable? = null
    ) {
        Timber.tag("ASSERT").wtf(throwable, message)
    }

    // Log a custom message with log level
    fun log(
        level: LogLevel,
        message: String,
        throwable: Throwable? = null
    ) {
        when (level) {
            LogLevel.VERBOSE -> v(message, throwable)
            LogLevel.DEBUG -> d(message, throwable)
            LogLevel.INFO -> i(message, throwable)
            LogLevel.WARN -> w(message, throwable)
            LogLevel.ERROR -> e(message, throwable)
            LogLevel.ASSERT -> wtf(message, throwable)
        }
    }
}
