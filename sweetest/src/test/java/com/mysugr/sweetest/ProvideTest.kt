package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.util.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`

class ProvideTest {

    companion object {
        lateinit var moduleTestingConfiguration: ModuleTestingConfiguration
    }

    interface TestUserService {
        fun getUserName(): String
    }

    class TestViewModel(
        private val userService: TestUserService
    ) {
        fun reload() {
            message = "Hello ${userService.getUserName()}!"
        }

        var message: String = "..."
            private set
    }

    class TestUserServiceFakeSteps(testContext: TestContext) :
        BaseSteps(testContext, moduleTestingConfiguration) {

        override fun configure() = super.configure()
            .provide<TestUserService> { FakeTestUserService() }

        private var username: String = "TEST"

        fun givenUsername(username: String) {
            this.username = username
        }

        private inner class FakeTestUserService : TestUserService {
            override fun getUserName(): String = username
        }
    }

    /**
     * This test class exists only to verify that `provide` can work together with the deprecated
     * `requireMock`. This test class and the corresponding test can be removed when `requireMock`
     * is removed.
     */
    class TestUserServiceMockSteps(testContext: TestContext) :
        BaseSteps(testContext, moduleTestingConfiguration) {

        override fun configure() = super.configure()
            .requireMock<TestUserService>()
            .onSetUp {
                `when`(instance.getUserName()).thenAnswer { username }
            }

        private val instance by dependency<TestUserService>()

        private var username: String = "TEST"

        fun givenUsername(username: String) {
            this.username = username
        }
    }

    class TestViewModelSteps(testContext: TestContext) :
        BaseSteps(testContext, moduleTestingConfiguration) {
        override fun configure() = super.configure()
            .provide<TestViewModel>()

        private val instance by dependency<TestViewModel>()

        fun whenReload() {
            instance.reload()
        }

        fun thenMessageIs(expectedMessage: String) {
            assertEquals(expectedMessage, instance.message)
        }
    }

    class TestClassWithFakeSteps : BaseJUnitTest(moduleTestingConfiguration) {
        private val userService by steps<TestUserServiceFakeSteps>()
        private val viewModel by steps<TestViewModelSteps>()

        fun exampleTest() {
            userService.givenUsername("exampleFoo")
            viewModel.whenReload()
            viewModel.thenMessageIs("Hello exampleFoo!")
        }
    }

    class TestClassWithMockSteps : BaseJUnitTest(moduleTestingConfiguration) {
        private val userService by steps<TestUserServiceMockSteps>()
        private val viewModel by steps<TestViewModelSteps>()

        fun exampleTest() {
            userService.givenUsername("exampleBar")
            viewModel.whenReload()
            viewModel.thenMessageIs("Hello exampleBar!")
        }
    }

    class TestClassWithDoubleProvide : BaseJUnitTest(moduleTestingConfiguration) {
        override fun configure() = super.configure()
            .provide<TestViewModel> { TestViewModel(mock()) }
            .provide<TestViewModel>()

        val instance by dependency<TestViewModel>()
    }

    @Before
    fun setUp() {
        TestEnvironment.fullReset()
    }

    @Test
    fun `when configured with any, provides can be used`() {
        givenConfiguredWithAny()
        // This test class uses `TestUserServiceFakeSteps`, which uses `provide {...}` for
        // `TestUserService`.
        // Test service is then used in `TestViewModelSteps` to create a real instance of
        // `TestViewModel` using `provide<TestViewModel>()`.
        TestClassWithFakeSteps().run {
            junitBefore()
            exampleTest()
        }
    }

    @Test
    fun `provides works together with mockOnly`() {
        givenServiceConfiguredAsMockOnly()
        // This test class uses `TestUserServiceMockSteps`, which uses `requireMock` for
        // `TestUserService`.
        // Test service is then used in `TestViewModelSteps` to create a real instance of
        // `TestViewModel` using `provide<TestViewModel>()`.
        TestClassWithMockSteps().run {
            junitBefore()
            exampleTest()
        }
    }

    @Test(expected = RuntimeException::class)
    fun `no dependencies configured leads to exception`() {
        givenNothingConfigured()
        TestClassWithFakeSteps().run {
            junitBefore()
            exampleTest()
        }
    }

    @Test(expected = RuntimeException::class)
    fun `provide cannot be used twice for the same type`() {
        givenConfiguredWithAny()
        TestClassWithDoubleProvide().run {
            junitBefore()
        }
    }

    private fun givenNothingConfigured() {
        moduleTestingConfiguration = moduleTestingConfiguration { }
    }

    private fun givenConfiguredWithAny() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<TestUserService>()
            dependency any of<TestViewModel>()
        }
    }

    private fun givenServiceConfiguredAsMockOnly() {
        moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<TestUserService>()
            dependency any of<TestViewModel>()
        }
    }
}
