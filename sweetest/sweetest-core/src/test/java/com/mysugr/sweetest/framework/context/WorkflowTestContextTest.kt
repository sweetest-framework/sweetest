package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import com.mysugr.sweetest.usecases.subscribeWorkflow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class WorkflowTestContextTest {

    val stepsContext = Mockito.mock(StepsTestContext::class.java)
    val sut = WorkflowTestContext(stepsContext)

    val trackedEvents = mutableListOf<String>()

    @Test
    fun `Runs through all steps`() {

        trackEvents()

        proceedWorkflow(sut)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES",
                "BEFORE_SET_UP",
                "SET_UP",
                "AFTER_SET_UP",
                "RUNNING"
            ), trackedEvents
        )

        finishWorkflow(sut)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES",
                "BEFORE_SET_UP",
                "SET_UP",
                "AFTER_SET_UP",
                "RUNNING",
                "TEAR_DOWN",
                "AFTER_TEAR_DOWN"
            ), trackedEvents
        )
    }

    @Test
    fun `Runs through part of steps`() {

        trackEvents()

        proceedWorkflow(sut, InitializationStep.INITIALIZE_STEPS)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS"
            ), trackedEvents
        )

        proceedWorkflow(sut, InitializationStep.INITIALIZE_DEPENDENCIES)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES"
            ), trackedEvents
        )
    }

    @Test
    fun `Doesn't skip steps`() {
        trackEvents()

        proceedWorkflow(sut, InitializationStep.INITIALIZE_STEPS)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS"
            ), trackedEvents
        )

        proceedWorkflow(sut, InitializationStep.SET_UP)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES",
                "BEFORE_SET_UP",
                "SET_UP"
            ), trackedEvents
        )
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe to INITIALIZE_FRAMEWORK`() {
        subscribeWorkflow(sut, InitializationStep.INITIALIZE_FRAMEWORK) { }
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe to DONE`() {
        subscribeWorkflow(sut, InitializationStep.DONE) { }
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe step already executed`() {
        proceedWorkflow(sut, InitializationStep.INITIALIZE_STEPS)
        subscribeWorkflow(sut, InitializationStep.INITIALIZE_STEPS) { }
    }

    @Test(expected = Exception::class)
    fun `Can't proceed to to DONE`() {
        proceedWorkflow(sut, InitializationStep.DONE)
    }

    @Test(expected = Exception::class)
    fun `Can't proceed to step already executed`() {
        proceedWorkflow(sut, InitializationStep.INITIALIZE_DEPENDENCIES)
        proceedWorkflow(sut, InitializationStep.INITIALIZE_DEPENDENCIES)
    }

    @Test
    fun `Can add handler during execution of same event`() {
        var executed = false
        subscribeWorkflow(sut, InitializationStep.INITIALIZE_STEPS) {
            subscribeWorkflow(sut, InitializationStep.INITIALIZE_STEPS) {
                executed = true
            }
        }
        proceedWorkflow(sut, InitializationStep.INITIALIZE_STEPS)

        assertTrue(executed)
    }

    private fun trackEvents() {
        subscribeWorkflow(sut, InitializationStep.INITIALIZE_STEPS) { trackedEvents += "INITIALIZE_STEPS" }
        subscribeWorkflow(sut, InitializationStep.INITIALIZE_DEPENDENCIES) { trackedEvents += "INITIALIZE_DEPENDENCIES" }
        subscribeWorkflow(sut, InitializationStep.BEFORE_SET_UP) { trackedEvents += "BEFORE_SET_UP" }
        subscribeWorkflow(sut, InitializationStep.SET_UP) { trackedEvents += "SET_UP" }
        subscribeWorkflow(sut, InitializationStep.AFTER_SET_UP) { trackedEvents += "AFTER_SET_UP" }
        subscribeWorkflow(sut, InitializationStep.RUNNING) { trackedEvents += "RUNNING" }
        subscribeWorkflow(sut, InitializationStep.TEAR_DOWN) { trackedEvents += "TEAR_DOWN" }
        subscribeWorkflow(sut, InitializationStep.AFTER_TEAR_DOWN) { trackedEvents += "AFTER_TEAR_DOWN" }

        `when`(stepsContext.finalizeSetUp()).then {
            trackedEvents += "finalize steps setup"
            Unit
        }
    }
}
