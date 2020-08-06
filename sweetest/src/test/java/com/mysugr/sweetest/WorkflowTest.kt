package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkflowTest {

    @Test
    fun `Normal test workflow`() {

        val trackedEvents = mutableListOf<String>()

        val test = object : BaseJUnitTest(moduleTestingConfiguration()) {
            override fun configure() = super.configure()
                .onInitializeSteps { trackedEvents += "initializeSteps" }
                .onInitializeDependencies { trackedEvents += "initializeDependencies" }
                .onSetUp { trackedEvents += "setUp" }
                .onTearDown { trackedEvents += "tearDown" }

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
                "setUp",
                "run test function",
                "tearDown"
            ), trackedEvents
        )
    }
}