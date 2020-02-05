package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.ContinuationInterceptor

interface CoroutinesTestContext {

    val coroutineScope: CoroutineScope

    fun runTest(testBody: suspend () -> Unit)

    companion object {

        val coroutineDispatcher
            get() = getCurrentInstance()
                .coroutineScope
                .coroutineContext[ContinuationInterceptor] as CoroutineDispatcher

        private var currentInstance: CoroutinesTestContext? = null

        internal fun setCurrentInstance(coroutinesTestContext: CoroutinesTestContext?) {
            currentInstance = coroutinesTestContext
        }

        private fun getCurrentInstance(): CoroutinesTestContext =
            currentInstance ?: throw CoroutinesUninitializedException()
    }
}