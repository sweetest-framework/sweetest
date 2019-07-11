package com.mysugr.sweetest.v2.framework.core.exceptions

import java.lang.Exception

open class HintsException @PublishedApi internal constructor(
    baseError: String,
    vararg hints: String
) : Exception() {

    override val message = generateMessage(baseError, hints)
}

private fun generateMessage(baseError: String, hints: Array<out String>): String {
    return StringBuilder().apply {
        appendln(baseError)
        if (hints.isNotEmpty()) {
            appendln()
            hints.forEach {
                appendln("• it")
            }
        }
    }.toString()
}