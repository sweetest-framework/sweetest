package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.ContinuationInterceptor

internal class CoroutinesTestContextProvider(workflowTestContext: WorkflowTestContext) {

    val coroutineScope: CoroutineScope
        get() = getDelegate().coroutineScope

    private var delegate: CoroutinesTestContext? = null
    private val isInitialized get() = delegate != null
    private val configuration = CoroutinesTestConfiguration()
    private val autoSetMainCoroutineDispatcherEnabled get() = configuration.data.autoSetMainCoroutineDispatcher ?: true

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
        autoSetMainCoroutineDispatcher()
    }

    private fun finish() {
        try {
            getDelegate().finish()
        } finally {
            setDelegate(null)
            autoResetMainCoroutineDispatcher()
        }
    }

    private fun autoSetMainCoroutineDispatcher() {
        if (autoSetMainCoroutineDispatcherEnabled) {
            val dispatcher =
                getDelegate().coroutineScope.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
            Dispatchers.setMain(dispatcher)
        }
    }

    private fun autoResetMainCoroutineDispatcher() {
        if (autoSetMainCoroutineDispatcherEnabled) {
            Dispatchers.resetMain()
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