package com.mysugr.sweetest.v2.framework.core.exceptions

import java.lang.Exception

class ShallowException @PublishedApi internal constructor(message: String, cause: Throwable) :
    Exception(message, cause) {
    override fun fillInStackTrace() = this // suppress stack trace generation
}