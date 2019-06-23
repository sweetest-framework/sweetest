package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.util.isMock
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DependenciesTest {

    companion object {
        lateinit var moduleTestingConfiguration: ModuleTestingConfiguration
    }

    class AService

    class BViewModel

    class ASteps(testContext: TestContext) : BaseSteps(testContext, moduleTestingConfiguration) {
        val instance by dependency<AService>()
    }

    class BSteps(testContext: TestContext) : BaseSteps(testContext, moduleTestingConfiguration) {
        val instance by dependency<BViewModel>()
    }

    class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
        val a by steps<ASteps>()
        val b by steps<BSteps>()
    }

    @Before
    fun setUp() { }

    @Test(expected = Throwable::class)
    fun `No dependencies configured leads to exception`() {
        givenNothingConfigured()
        TestClass().run {
            junitBefore()
        }
    }

    @Test
    fun `A can be used as mock`() {
        givenAMockOnly()
        TestClass().run {
            junitBefore()
            assertTrue(a.instance.isMock)
        }
    }

    private fun givenNothingConfigured() {
        moduleTestingConfiguration = moduleTestingConfiguration { }
    }

    private fun givenAMockOnly() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<AService>()
            dependency realOnly of<BViewModel>()
        }
    }
}