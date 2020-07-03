package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.util.isMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DependenciesTest {

    companion object {
        lateinit var moduleTestingConfiguration: ModuleTestingConfiguration
        lateinit var initializedAInstance: AService
    }

    class AService

    class BViewModel

    class ASteps(testContext: TestContext) : BaseSteps(testContext, moduleTestingConfiguration) {
        val instance by dependency<AService>()
    }

    class BSteps(testContext: TestContext) : BaseSteps(testContext, moduleTestingConfiguration) {
        val instance by dependency<BViewModel>()
    }

    class CSteps(testContext: TestContext) : BaseSteps(testContext) {
        val instance by dependency<BViewModel>()
    }

    class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
        val a by steps<ASteps>()
        val b by steps<BSteps>()
    }

    class TestClassAReal : BaseJUnitTest(moduleTestingConfiguration) {
        val a by steps<ASteps>()
        val b by steps<BSteps>()
        override fun configure() = super.configure()
            .requireReal<AService>()
    }

    class TestClassBMock : BaseJUnitTest(moduleTestingConfiguration) {
        val a by steps<ASteps>()
        val b by steps<BSteps>()
        override fun configure() = super.configure()
            .requireMock<BViewModel>()
    }

    class TestClassNoConfig : BaseJUnitTest() {
        val c by steps<CSteps>()

        override fun configure() = super.configure()
            .provide<BViewModel>()
    }

    class TestClassMixedConfig : BaseJUnitTest() {
        val a by steps<ASteps>()
        val c by steps<CSteps>()

        override fun configure() = super.configure()
            .requireReal<BViewModel>()
            .requireMock<AService>()
    }

    @Before
    fun setUp() {
        TestEnvironment.fullReset()
    }

    @Test(expected = Throwable::class)
    fun `No dependencies configured leads to exception`() {
        givenNothingConfigured()
        TestClass().run {
            junitBefore()
        }
    }

    @Test
    fun `A can be used as mock`() {
        givenAMockBReal()
        TestClass().run {
            junitBefore()
            assertTrue(a.instance.isMock)
        }
    }

    @Test
    fun `B is real`() {
        givenAMockBReal()
        TestClass().run {
            junitBefore()
            assertFalse(b.instance.isMock)
        }
    }

    @Test(expected = Throwable::class)
    fun `A is mockOnly, can't be used as real`() {
        givenAMockBReal()
        TestClassAReal().run {
            junitBefore()
        }
    }

    @Test(expected = Throwable::class)
    fun `B is realOnly, can't be used as mock`() {
        givenAMockBReal()
        TestClassBMock().run {
            junitBefore()
        }
    }

    @Test
    fun `A is default-configured initialized instance`() {
        givenAInitializedMockBReal()
        TestClass().run {
            junitBefore()
            a.instance // needs to be accessed in order to initialize the dependency
            assertNotNull(initializedAInstance)
            assertEquals(initializedAInstance, a.instance)
        }
    }

    @Test
    fun `No module configuration is provided, default mode is REAL`() {
        TestClassNoConfig().run {
            junitBefore()
            assertNotNull(c.instance)
            assertFalse(c.instance.isMock)
        }
    }

    @Test
    fun `No module configuration is provided mixed with module configuration provided`() {
        givenAMock()
        TestClassMixedConfig().run {
            junitBefore()
            assertNotNull(c.instance)
            assertNotNull(a.instance)
        }
    }

    private fun givenNothingConfigured() {
        moduleTestingConfiguration = moduleTestingConfiguration { }
    }

    private fun givenAMock() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<AService>()
        }
    }

    private fun givenAMockBReal() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<AService>()
            dependency realOnly of<BViewModel>()
        }
    }

    private fun givenAInitializedMockBReal() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly initializer { AService().also { initializedAInstance = it } }
            dependency realOnly of<BViewModel>()
        }
    }
}