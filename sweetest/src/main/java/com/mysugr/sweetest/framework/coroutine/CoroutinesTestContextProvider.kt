package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import kotlinx.coroutines.CoroutineScope

class CoroutinesTestContextProvider(workflowTestContext: WorkflowTestContext) :
    CoroutinesTestContext {

    private var delegate: CoroutinesTestContext? = null

    override val coroutineScope: CoroutineScope
        get() = getDelegate().coroutineScope

    init {
        workflowTestContext.subscribe(InitializationStep.SET_UP) {
            initializeLegacy()
        }
    }

    private fun initializeLegacy() {
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