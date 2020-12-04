package dev.sweetest.internal

class SweetestException @InternalSweetestApi constructor(message: String, cause: Throwable? = null) :
    Exception(message, cause)
