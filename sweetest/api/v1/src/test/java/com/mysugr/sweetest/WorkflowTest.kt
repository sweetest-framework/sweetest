@file:Suppress("DEPRECATION")

package com.mysugr.sweetest

import dev.sweetest.v1.BaseJUnitTest
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkflowTest {

    @Test
    fun `Normal test workflow`() {

        val trackedEvents = mutableListOf<String>()

        val test = object : BaseJUnitTest() {
            override fun configure() = super.configure()
                .onInitializeSteps { trackedEvents += "initializeSteps" }
                .onInitializeDependencies { trackedEvents += "initializeDependencies" }
                .onBeforeSetUp { trackedEvents += "beforeSetUp" }
                .onSetUp { trackedEvents += "setUp" }
                .onAfterSetUp { trackedEvents += "afterSetUp" }
                .onTearDown { trackedEvents += "tearDown" }
                .onAfterTearDown { trackedEvents += "afterTearDown" }

            fun testFunction() {
                trackedEvents += "run test function"
            }
        }

        test.junitBefore()
        test.testFunction()
        test.junitAfter()

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
