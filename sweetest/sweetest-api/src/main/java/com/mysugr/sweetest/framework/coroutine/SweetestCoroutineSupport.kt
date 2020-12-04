package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Public access to undocumented, experimental coroutines feature
 */
object SweetestCoroutineSupport {

    val coroutineDispatcher: CoroutineDispatcher by lazy {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }
}