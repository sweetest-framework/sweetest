package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.util.isMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

class DependenciesTest : BaseTest() {

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

    class TestClassAllSpy : BaseJUnitTest(moduleTestingConfiguration) {
        val a by steps<ASteps>()
        val b by steps<BSteps>()
        override fun configure() = super.configure()
            .requireSpy<AService>()
            .requireSpy<BViewModel>()
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

    @Test
    fun `Legacy bug compatibility - A is mockOnly, can be used as real nonetheless`() {
        givenAMockBReal()
        TestClassAReal().run {
            junitBefore()
        }
    }

    @Test
    fun `Legacy bug compatibility - B is realOnly, can be used as mock nonetheless`() {
        givenAMockBReal()
        TestClassBMock().run {
            junitBefore()
        }
    }

    @Test
    fun `Legacy bug compatibility - realOnly and mockOnly, can both be used as spy nonetheless`() {
        givenAMockBReal()
        TestClassAllSpy().run {
            junitBefore()
        }
    }

    @Test(expected = Throwable::class)
    @Ignore("Ignored because of legacy bug that is kept as is because of backwards compatibility")
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

    @Test(expected = RuntimeException::class)
    fun `Given module dependency configuration, changing REAL to MOCK throws no exception`() {
        givenAllAny()

        val testInstance = object : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .requireReal<AService>()
                .requireMock<AService>()
        }

        testInstance.junitBefore()
    }

    @Test(expected = RuntimeException::class)
    fun `Given module dependency configuration, changing MOCK to REAL throws exception`() {
        givenAllAny()

        val testInstance = object : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .requireMock<AService>()
                .requireReal<AService>()
        }

        testInstance.junitBefore()
    }

    @Test(expected = SweetestException::class)
    fun `No module configuration, changing REAL to MOCK throws exception`() {
        val testInstance = object : BaseJUnitTest() {
            override fun configure() = super.configure()
                .requireReal<AService>()
                .requireMock<AService>()
        }

        testInstance.junitBefore()
    }

    @Test(expected = SweetestException::class)
    fun `No module configuration, changing MOCK to REAL throws exception`() {
        var actualInstance: AService? = null

        val testInstance = object : BaseJUnitTest() {
            val instance by dependency<AService>()

            override fun configure() = super.configure()
                .requireMock<AService>()
                .requireReal<AService>()
                .onSetUp {
                    actualInstance = instance
                }
        }

        testInstance.junitBefore()
    }

    @Test(expected = SweetestException::class)
    fun `No module configuration or provide throws exception`() {
        var actualInstance: AService? = null

        val testInstance = object : BaseJUnitTest() {
            val instance by dependency<AService>()

            override fun configure() = super.configure()
                .onSetUp {
                    actualInstance = instance
                }
        }

        testInstance.junitBefore()
        assertFalse(actualInstance!!.isMock)
    }

    @Test
    fun `Given module configuration, returns MOCKed dependency by default`() {
        givenAllAny()

        var actualInstance: AService? = null

        val testInstance = object : BaseJUnitTest(moduleTestingConfiguration) {
            val instance by dependency<AService>()

            override fun configure() = super.configure()
                .onSetUp {
                    actualInstance = instance
                }
        }

        testInstance.junitBefore()
        assertTrue(actualInstance!!.isMock)
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
    fun `Dependency can be required to be of some mode multiple times`() {
        givenAllAny()

        val test = object : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                // A is real
                .requireReal<AService>()
                .requireReal<AService>()
                .offerReal<AService> { AService() }
                .offerReal<AService> { AService() }
                .offerRealRequired<AService> { AService() }
                .offerRealRequired<AService> { AService() }
                // B is mock
                .requireMock<BViewModel>()
                .requireMock<BViewModel>()
                .offerMock<BViewModel> { BViewModel() }
                .offerMock<BViewModel> { BViewModel() }
                .offerMockRequired<BViewModel> { BViewModel() }
                .offerMockRequired<BViewModel> { BViewModel() }
        }

        test.junitBefore()
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

    private fun givenAllAny() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<AService>()
            dependency any of<BViewModel>()
        }
    }
}