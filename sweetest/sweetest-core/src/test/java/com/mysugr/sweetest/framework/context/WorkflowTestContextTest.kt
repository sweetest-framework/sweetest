package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import com.mysugr.sweetest.usecases.subscribeWorkflow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class WorkflowTestContextTest {

    private val stepsContext = Mockito.mock(StepsTestContext::class.java)
    private val sut = WorkflowTestContext(stepsContext)
    private val trackedEvents = mutableListOf<String>()

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

        proceedWorkflow(sut, WorkflowStep.INITIALIZE_STEPS)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS"
            ), trackedEvents
        )

        proceedWorkflow(sut, WorkflowStep.INITIALIZE_DEPENDENCIES)

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

        proceedWorkflow(sut, WorkflowStep.INITIALIZE_STEPS)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS"
            ), trackedEvents
        )

        proceedWorkflow(sut, WorkflowStep.SET_UP)

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
        subscribeWorkflow(sut, WorkflowStep.INITIALIZE_FRAMEWORK) { }
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe to DONE`() {
        subscribeWorkflow(sut, WorkflowStep.DONE) { }
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe step already executed`() {
        proceedWorkflow(sut, WorkflowStep.INITIALIZE_STEPS)
        subscribeWorkflow(sut, WorkflowStep.INITIALIZE_STEPS) { }
    }

    @Test(expected = Exception::class)
    fun `Can't proceed to to DONE`() {
        proceedWorkflow(sut, WorkflowStep.DONE)
    }

    @Test(expected = Exception::class)
    fun `Can't proceed to step already executed`() {
        proceedWorkflow(sut, WorkflowStep.INITIALIZE_DEPENDENCIES)
        proceedWorkflow(sut, WorkflowStep.INITIALIZE_DEPENDENCIES)
    }

    @Test
    fun `Can add handler during execution of same event`() {
        var executed = false
        subscribeWorkflow(sut, WorkflowStep.INITIALIZE_STEPS) {
            subscribeWorkflow(sut, WorkflowStep.INITIALIZE_STEPS) {
                executed = true
            }
        }
        proceedWorkflow(sut, WorkflowStep.INITIALIZE_STEPS)

        assertTrue(executed)
    }

    private fun trackEvents() {
        subscribeWorkflow(sut, WorkflowStep.INITIALIZE_STEPS) { trackedEvents += "INITIALIZE_STEPS" }
        subscribeWorkflow(sut, WorkflowStep.INITIALIZE_DEPENDENCIES) { trackedEvents += "INITIALIZE_DEPENDENCIES" }
        subscribeWorkflow(sut, WorkflowStep.BEFORE_SET_UP) { trackedEvents += "BEFORE_SET_UP" }
        subscribeWorkflow(sut, WorkflowStep.SET_UP) { trackedEvents += "SET_UP" }
        subscribeWorkflow(sut, WorkflowStep.AFTER_SET_UP) { trackedEvents += "AFTER_SET_UP" }
        subscribeWorkflow(sut, WorkflowStep.RUNNING) { trackedEvents += "RUNNING" }
        subscribeWorkflow(sut, WorkflowStep.TEAR_DOWN) { trackedEvents += "TEAR_DOWN" }
        subscribeWorkflow(sut, WorkflowStep.AFTER_TEAR_DOWN) { trackedEvents += "AFTER_TEAR_DOWN" }

        `when`(stepsContext.finalizeSetUp()).then {
            trackedEvents += "finalize steps setup"
            Unit
        }
    }
}
