package com.mysugr.sweetest.v2.api

class InitializerScope {

    /**
     * Returns an instance of a dependency specified by the type of the generic argument (which can be in turn be
     * inferred, too)
     */
    inline fun <reified T> dependency(): T {
        throw NotImplementedError()
    }
}
