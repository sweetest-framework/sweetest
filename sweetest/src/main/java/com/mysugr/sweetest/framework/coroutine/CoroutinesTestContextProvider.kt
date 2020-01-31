package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import kotlinx.coroutines.CoroutineScope

internal class CoroutinesTestContextProvider(workflowTestContext: WorkflowTestContext) {

    val coroutineScope: CoroutineScope
        get() = getDelegate().coroutineScope

    private var delegate: CoroutinesTestContext? = null
    private val isInitialized get() = delegate != null
    private val configuration = CoroutinesTestConfiguration()

    init {
        initializeOnSetUpEvent(workflowTestContext)
        finishOnTearDownEvent(workflowTestContext)
    }

    fun configure(block: CoroutinesTestConfiguration.() -> Unit) {
        checkAlreadyInitialized()
        block(configuration)
    }

    private fun initializeOnSetUpEvent(workflowTestContext: WorkflowTestContext) {
        workflowTestContext.subscribe(InitializationStep.SET_UP) {
            this.initialize()
        }
    }

    private fun finishOnTearDownEvent(workflowTestContext: WorkflowTestContext) {
        workflowTestContext.subscribe(InitializationStep.TEAR_DOWN) {
            finish()
        }
    }

    private fun initialize() {
        checkAlreadyInitialized()
        if (configuration.data.useLegacyTestCoroutine) {
            setDelegate(LegacyCoroutinesTestContext())
        } else {
            setDelegate(DefaultCoroutinesTestContext())
        }
    }

    private fun finish() {
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
        check(!isInitialized) { "Can't perform operation, already initialized" }
    }
}