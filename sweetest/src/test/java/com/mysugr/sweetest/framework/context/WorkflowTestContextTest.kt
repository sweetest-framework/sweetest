package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep
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

        sut.run()

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES",
                "SET_UP",
                "RUNNING"
            ), trackedEvents
        )

        sut.finish()

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES",
                "SET_UP",
                "RUNNING",
                "TEAR_DOWN"
            ), trackedEvents
        )
    }

    @Test
    fun `Runs through part of steps`() {

        trackEvents()

        sut.proceedTo(InitializationStep.INITIALIZE_STEPS)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS"
            ), trackedEvents
        )

        sut.proceedTo(InitializationStep.INITIALIZE_DEPENDENCIES)

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

        sut.proceedTo(InitializationStep.INITIALIZE_STEPS)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS"
            ), trackedEvents
        )

        sut.proceedTo(InitializationStep.SET_UP)

        assertEquals(
            listOf(
                "INITIALIZE_STEPS",
                "finalize steps setup",
                "INITIALIZE_DEPENDENCIES",
                "SET_UP"
            ), trackedEvents
        )
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe to INITIALIZE_FRAMEWORK`() {
        sut.subscribe(InitializationStep.INITIALIZE_FRAMEWORK) { }
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe to DONE`() {
        sut.subscribe(InitializationStep.DONE) { }
    }

    @Test(expected = Exception::class)
    fun `Can't subscribe step already executed`() {
        sut.proceedTo(InitializationStep.INITIALIZE_STEPS)
        sut.subscribe(InitializationStep.INITIALIZE_STEPS) { }
    }

    @Test(expected = Exception::class)
    fun `Can't proceed to to DONE`() {
        sut.proceedTo(InitializationStep.DONE)
    }

    @Test(expected = Exception::class)
    fun `Can't proceed to step already executed`() {
        sut.proceedTo(InitializationStep.INITIALIZE_DEPENDENCIES)
        sut.proceedTo(InitializationStep.INITIALIZE_DEPENDENCIES)
    }

    @Test
    fun `Can add handler during execution of same event`() {
        var executed = false
        sut.subscribe(InitializationStep.INITIALIZE_STEPS) {
            sut.subscribe(InitializationStep.INITIALIZE_STEPS) {
                executed = true
            }
        }
        sut.proceedTo(InitializationStep.INITIALIZE_STEPS)

        assertTrue(executed)
    }

    private fun trackEvents() {
        sut.subscribe(InitializationStep.INITIALIZE_STEPS) { trackedEvents += "INITIALIZE_STEPS" }
        sut.subscribe(InitializationStep.INITIALIZE_DEPENDENCIES) { trackedEvents += "INITIALIZE_DEPENDENCIES" }
        sut.subscribe(InitializationStep.SET_UP) { trackedEvents += "SET_UP" }
        sut.subscribe(InitializationStep.RUNNING) { trackedEvents += "RUNNING" }
        sut.subscribe(InitializationStep.TEAR_DOWN) { trackedEvents += "TEAR_DOWN" }

        `when`(stepsContext.finalizeSetUp()).then {
            trackedEvents += "finalize steps setup"
            Unit
        }
    }
}
