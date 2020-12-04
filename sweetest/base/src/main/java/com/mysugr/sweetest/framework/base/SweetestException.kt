package com.mysugr.sweetest.framework.base

import dev.sweetest.internal.InternalSweetestApi
import java.lang.Exception

class SweetestException @InternalSweetestApi constructor(message: String, cause: Throwable? = null) :
    Exception(message, cause)
