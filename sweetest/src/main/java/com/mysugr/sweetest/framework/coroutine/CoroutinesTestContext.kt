package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import kotlinx.coroutines.test.TestCoroutineScope

internal class CoroutinesTestContext(private val workflowTestContext: WorkflowTestContext) {

    val testCoroutineScope: TestCoroutineScope
        get() = lazyGetTestCoroutineScope()

    private var _testCoroutineScope: TestCoroutineScope? = null
    private var cleanedUp: Boolean = false

    init {
        workflowTestContext.subscribe(InitializationStep.DONE) { cleanUp() }
    }

    private fun lazyGetTestCoroutineScope(): TestCoroutineScope {
        check(!cleanedUp) {
            "The TestCoroutineScope has already been cleaned up and thus can't be used anymore"
        }
        synchronized(this) {
            if (_testCoroutineScope == null) {
                check (workflowTestContext.currentStep.isBefore(InitializationStep.DONE)) {
                    "Can't initialize TestCoroutineScope when testing workflow is already done"
                }
                _testCoroutineScope = TestCoroutineScope()
            }
            return _testCoroutineScope!!
        }
    }

    private fun cleanUp() {
        check(!cleanedUp) {
            "The TestCoroutineScope has already been cleaned " +
                "up and thus can't be cleaned up anymore"
        }
        _testCoroutineScope?.cleanupTestCoroutines()
        cleanedUp = true
    }
}