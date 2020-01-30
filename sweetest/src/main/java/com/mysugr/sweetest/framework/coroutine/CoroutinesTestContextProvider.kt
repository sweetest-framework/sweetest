package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

class CoroutinesTestContextProvider : CoroutinesTestContext {

    private var delegate: CoroutinesTestContext? = null

    override val coroutineDispatcher: CoroutineDispatcher
        get() = getDelegate().coroutineDispatcher

    override val coroutineContext: CoroutineContext
        get() = getDelegate().coroutineContext

    fun initializeLegacy() {
        checkAlreadyInitialized()
        setDelegate(LegacyCoroutinesTestContext())
    }

    override suspend fun finish() {
        try {
            getDelegate().finish()
        } finally {
            setDelegate(null)
        }
    }

    private fun getDelegate(): CoroutinesTestContext =
        delegate ?: error(
            "Coroutine support is not initialized, please use sweetest's " +
                "runBlockingTest extension in your test class!"
        )

    private fun setDelegate(coroutinesTestContext: CoroutinesTestContext?) {
        delegate = coroutinesTestContext
        CoroutinesTestContext.setCurrentInstance(coroutinesTestContext)
    }

    private fun checkAlreadyInitialized() {
        check(delegate == null) { "Already initialized" }
    }
}