package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.ContinuationInterceptor

internal class CoroutinesTestContextWrapper(workflowTestContext: WorkflowTestContext) : CoroutinesTestContext {

    override val coroutineScope: CoroutineScope get() = getDelegate().coroutineScope

    private var delegate: CoroutinesTestContext? = null
    private val isInitialized get() = delegate != null

    private val configuration = CoroutinesTestConfiguration()

    init {
        initializeOnSetUpEvent(workflowTestContext)
        finishOnTearDownEvent(workflowTestContext)
    }

    fun configure(block: CoroutinesTestConfigurator.() -> Unit) {
        checkAlreadyInitialized()
        block(configuration)
    }

    override fun runTest(testBody: suspend () -> Unit) {
        getDelegate().runTest(testBody)
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
            setDelegate(LegacyCoroutinesTestContext(configuration.data))
        } else {
            setDelegate(DefaultCoroutinesTestContext(configuration.data))
        }
        autoSetMainCoroutineDispatcher()
    }

    private fun finish() {
        getDelegate() // make sure it was initialized before
        setDelegate(null)
        autoResetMainCoroutineDispatcher()
    }

    private fun autoSetMainCoroutineDispatcher() {
        if (configuration.data.autoSetMainCoroutineDispatcherEnabled) {
            val dispatcher =
                getDelegate().coroutineScope.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
            Dispatchers.setMain(dispatcher)
        }
    }

    private fun autoResetMainCoroutineDispatcher() {
        if (configuration.data.autoSetMainCoroutineDispatcherEnabled) {
            Dispatchers.resetMain()
        }
    }

    private fun getDelegate(): CoroutinesTestContext =
        delegate ?: throw CoroutinesUninitializedException()

    private fun setDelegate(coroutinesTestContext: CoroutinesTestContext?) {
        delegate = coroutinesTestContext
        CoroutinesTestContext.setCurrentInstance(coroutinesTestContext)
    }

    private fun checkAlreadyInitialized() {
        check(!isInitialized) { "Can't perform operation, already initialized" }
    }
}