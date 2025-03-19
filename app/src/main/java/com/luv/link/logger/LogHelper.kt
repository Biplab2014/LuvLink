package com.luv.link.logger

import android.util.Log
import com.luv.link.global.config.GlobalConfig.nibble_package
import com.luv.link.utils.FilesUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope

class LogHelper
@Inject
constructor() : NibLogger {
    private var tag: String = "LogHelper"
    private var threadNameLength = 6
    private var classNameLength = 20
    private var isTimeVisible = false
    private var methodNameLength = 30
    private var isSpacingEnabled = false
    private var isClassNameVisible = true
    private var isLengthShouldWrapped = true
    private var isMethodNameVisible = true
    private var timeFormat = "HH:mm:ss.SSS"
    private var isThreadNameVisible = false
    private var isPackageNameVisible = false
    private var packageAndClassNameLength = 35

    override fun logsIt(
        priority: Int,
        customTag: String?,
        msg: String,
        t: Throwable?
    ) {
        val stackTrace = Thread.currentThread().stackTrace
        val elementIndex: Int = getElementIndex(stackTrace)
        if (elementIndex == 0) return

        val element = stackTrace[elementIndex]
        val result = StringBuilder()

        if (isTimeVisible) result.append(getTime()).append(" || ")
        if (isThreadNameVisible) result.append("T:").append(getThreadId()).append(" | ")
        if (isClassNameVisible) addClassName(element, result)
        if (isMethodNameVisible) addMethodName(element, result)

        if (msg.isNotEmpty()) {
            result.append(" || MESSAGE :: ")
            result.append(msg)
        }
        // addFunctionNameIfNotNull(functionName, result)
        addExceptionIfNotNull(t, result)
        val tag = if (customTag == null) tag else "$customTag"
        Log.println(priority, "INC", "$tag || ID : $result")
        FilesUtil.logInSdcard(tag, result.toString(), GlobalScope)
    }

    private fun getElementIndex(stackTrace: Array<StackTraceElement>?): Int {
        if (stackTrace == null) return 0
        for (i in 2..stackTrace.size) {
            val className = stackTrace[i].className ?: ""
            if (className.contains(this.javaClass.simpleName)) continue
            return i
        }
        return 0
    }

    private fun getThreadId(): StringBuilder? {
        val name = Thread.currentThread().name
        val threadId = StringBuilder(name)
        addSpaces(threadId, threadNameLength - name.length)
        return threadId
    }

    private fun addClassName(
        element: StackTraceElement,
        result: StringBuilder
    ) {
        val fullClassName = element.className
        val maxLength =
            if (isPackageNameVisible) packageAndClassNameLength else classNameLength

        var classNameFormatted =
            if (isPackageNameVisible) {
                fullClassName.replace(nibble_package, "")
            } else {
                fullClassName.substring(fullClassName.lastIndexOf('.') + 1)
            }

        if (isLengthShouldWrapped) {
            classNameFormatted =
                wrapString(
                    classNameFormatted,
                    maxLength
                )
        }
        result.append(classNameFormatted)

        addSpaces(result, maxLength - classNameFormatted.length)
        result.append(" || ")
    }

    private fun addMethodName(
        element: StackTraceElement,
        result: StringBuilder
    ) {
        var methodName = element.methodName
        if (isLengthShouldWrapped) methodName = wrapString(methodName, methodNameLength)
        result.append("$methodName()")
        addSpaces(result, methodNameLength - methodName.length)
    }

    private fun addExceptionIfNotNull(
        t: Throwable?,
        result: StringBuilder
    ) {
        if (t != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.flush()
            result.append("\n Throwable: ")
            result.append(sw.toString())
        }
    }

    private fun wrapString(
        string: String,
        maxLength: Int
    ): String = string.substring(0, string.length.coerceAtMost(maxLength))

    private fun addSpaces(
        result: StringBuilder,
        spaces: Int
    ) {
        if (isSpacingEnabled && spaces > 0) result.append(" ".repeat(spaces))
    }

    private fun getTime(): String? {
        val df: DateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        return df.format(Calendar.getInstance().time)
    }

    override fun error(
        customTag: String?,
        msg: String,
        t: Throwable?
    ) {
        logsIt(Log.ERROR, customTag, msg, t)
    }

    override fun debug(
        tag: String?,
        msg: String
    ) {
        logsIt(Log.DEBUG, tag, msg, null)
    }
}
