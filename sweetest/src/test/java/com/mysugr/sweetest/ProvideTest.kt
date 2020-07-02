package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyConfigurations
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.util.isMock
import com.mysugr.sweetest.util.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProvideTest {

    interface TestUserService

    class FakeTestUserService : TestUserService

    class TestViewModel(internal val userService: TestUserService)

    @Before
    fun setUp() {
        TestEnvironment.fullReset()
    }

    @Test
    fun `provide can be used when dependency is configured with any`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<TestUserService>()
            dependency any of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide<TestUserService> { FakeTestUserService() }
                .provide<TestViewModel>()

            val userService by dependency<TestUserService>()
            val viewModel by dependency<TestViewModel>()
        }

        TestClass().run {
            junitBefore()

            assertEquals(TestViewModel::class.java, viewModel::class.java)
            assertEquals(FakeTestUserService::class.java, userService::class.java)
            assertEquals(userService, viewModel.userService)
        }
    }

    /**
     * Note: This test makes use of a bug: `realOnly` and `mockOnly` are not enforced.
     */
    @Test
    fun `provide can be used when dependency is configured with mockOnly or realOnly`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency realOnly of<TestUserService>()
            dependency mockOnly of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide<TestUserService> { FakeTestUserService() }
                .provide<TestViewModel>()

            val userService by dependency<TestUserService>()
            val viewModel by dependency<TestViewModel>()
        }

        TestClass().run {
            junitBefore()

            assertEquals(TestViewModel::class.java, viewModel::class.java)
            assertEquals(FakeTestUserService::class.java, userService::class.java)
            assertEquals(userService, viewModel.userService)
            assertFalse(viewModel.isMock)
        }
    }

    @Test
    fun `provide works together with mockOnly and requireMock`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<TestUserService>()
            dependency any of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .requireMock<TestUserService>()
                .provide<TestViewModel>()

            val viewModel by dependency<TestViewModel>()
        }

        TestClass().run {
            junitBefore()

            assertEquals(TestViewModel::class.java, viewModel::class.java)
            assertTrue(viewModel.userService.isMock)
        }
    }

    @Test(expected = DependencyConfigurations.NotFoundException::class)
    fun `exception if dependency is not declared in module configuration`() {
        val moduleTestingConfiguration = moduleTestingConfiguration { }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide<TestUserService> { FakeTestUserService() }

            val userService by dependency<TestUserService>()
        }

        TestClass().run {
            junitBefore()
            userService // access dependency
        }
    }

    @Test(expected = RuntimeException::class)
    fun `provide cannot be used twice for the same type`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide { TestViewModel(mock()) }
                .provide<TestViewModel>()
        }

        TestClass().run {
            junitBefore()
        }
    }
}
