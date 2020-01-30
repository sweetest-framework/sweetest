package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

interface CoroutinesTestContext {
    val coroutineDispatcher get() = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
    val coroutineContext: CoroutineContext

    suspend fun finish()

    companion object {

        val coroutineDispatcher get() = getCurrentInstance().coroutineDispatcher

        private var currentInstance: CoroutinesTestContext? = null

        internal fun setCurrentInstance(coroutinesTestContext: CoroutinesTestContext?) {
            currentInstance = coroutinesTestContext
        }

        private fun getCurrentInstance(): CoroutinesTestContext =
            currentInstance ?: error(
                "CoroutinesTestContext is not set, please make sure you don't " +
                    "access coroutineDispatcher or coroutineContext prematurely"
            )
    }
}