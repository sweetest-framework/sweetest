package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.framework.coroutine.CoroutinesUninitializedException
import com.mysugr.sweetest.framework.flow.InitializationStep
import org.junit.Test

class CoroutinesTestContextWrapperTest {

    private val testBuilder = TestBuilder(moduleTestingConfiguration())

    // --- accessing coroutine scope

    @Test(expected = CoroutinesUninitializedException::class)
    fun `Coroutine scope is not initialized before framework initialization`() {
        try {
            testBuilder.build()
            getCoroutineScope() // should throw
        } finally {
            proceedToEnd()
        }
    }

    @Test
    fun `Coroutine scope is initialized at framework initialization`() {
        try {
            testBuilder.build()
            proceedToFrameworkInitialization()
            getCoroutineScope() // should not throw
        } finally {
            proceedToEnd()
        }
    }

    @Test(expected = CoroutinesUninitializedException::class)
    fun `Coroutine scope is not available after the test workflow`() {
        testBuilder.build()
        proceedToEnd()
        getCoroutineScope() // throws
    }

    // --- accessing CoroutinesTestContext.coroutineDispatcher

    @Test(expected = CoroutinesUninitializedException::class)
    fun `CoroutinesTestContext is not initialized before framework initialization`() {
        try {
            testBuilder.build()
            CoroutinesTestContext.coroutineDispatcher // should throw
        } finally {
            proceedToEnd()
        }
    }

    @Test(expected = CoroutinesUninitializedException::class)
    fun `CoroutinesTestContext is not available after the test workflow`() {
        testBuilder.build()
        proceedToEnd()
        CoroutinesTestContext.coroutineDispatcher // should throw
    }

    // --- configuration

    @Test()
    fun `Can configure before initialization`() {
        testBuilder.build()

        accessConfiguration()
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't configure after initialization`() {
        testBuilder.build()
        testBuilder.testContext.workflow.proceedTo(InitializationStep.INITIALIZE_FRAMEWORK)

        accessConfiguration() // should throw
    }

    // ---

    private fun proceedToFrameworkInitialization() {
        testBuilder.testContext.workflow.proceedTo(InitializationStep.INITIALIZE_FRAMEWORK)
    }

    private fun proceedToEnd() {
        testBuilder.testContext.workflow.finish()
    }

    private fun accessConfiguration() {
        testBuilder.testContext.coroutines.configure { }
    }

    private fun getCoroutineScope() {
        testBuilder.testContext.coroutines.coroutineScope
    }
}