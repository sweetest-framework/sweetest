package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineScope

class CoroutinesTestContextProvider : CoroutinesTestContext {

    private var delegate: CoroutinesTestContext? = null

    override val coroutineScope: CoroutineScope
        get() = getDelegate().coroutineScope

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