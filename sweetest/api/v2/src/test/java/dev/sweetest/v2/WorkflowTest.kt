package dev.sweetest.v2

import org.junit.Assert.assertEquals
import org.junit.Test

class WorkflowTest : AutoWipeTest() {

    @Test
    fun `Normal test workflow`() {

        val trackedEvents = mutableListOf<String>()

        val test = object : BaseTest() {
            init {
                onInitializeSteps { trackedEvents += "initializeSteps" }
                onInitializeDependencies { trackedEvents += "initializeDependencies" }
                onBeforeSetUp { trackedEvents += "beforeSetUp" }
                onSetUp { trackedEvents += "setUp" }
                onAfterSetUp { trackedEvents += "afterSetUp" }
                onTearDown { trackedEvents += "tearDown" }
                onAfterTearDown { trackedEvents += "afterTearDown" }
            }

            fun testFunction() {
                trackedEvents += "run test function"
            }
        }

        test.startWorkflow()
        test.testFunction()
        test.finishWorkflow()

        assertEquals(
            listOf(
                "initializeSteps",
                "initializeDependencies",
                "beforeSetUp",
                "setUp",
                "afterSetUp",
                "run test function",
                "tearDown",
                "afterTearDown"
            ), trackedEvents
        )
    }
}
