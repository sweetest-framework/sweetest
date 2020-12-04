@file:Suppress("DEPRECATION")

package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.util.isMock
import com.mysugr.sweetest.util.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProvideTest : AutoWipeTest() {

    interface TestUserService

    class FakeTestUserService : TestUserService

    class TestViewModel(internal val userService: TestUserService)

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

    @Test
    fun `no exception if dependency is not declared in module configuration`() {
        class TestClass : BaseJUnitTest() {
            override fun configure() = super.configure()
                .provide<TestUserService> { FakeTestUserService() }

            val userService by dependency<TestUserService>()
        }

        TestClass().run {
            junitBefore()
            userService // access dependency
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (once with, once without lambda)`() {
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

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (once without, once with lambda)`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide<TestViewModel>()
                .provide { TestViewModel(mock()) }
        }

        TestClass().run {
            junitBefore()
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (with lambda)`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide { TestViewModel(mock()) }
                .provide { TestViewModel(mock()) }
        }

        TestClass().run {
            junitBefore()
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (without lambda)`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency any of<TestViewModel>()
        }

        class TestClass : BaseJUnitTest(moduleTestingConfiguration) {
            override fun configure() = super.configure()
                .provide<TestViewModel>()
                .provide<TestViewModel>()
        }

        TestClass().run {
            junitBefore()
        }
    }

    @Test
    fun `provide without lambda overrides global mockOnly configuration`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<FakeTestUserService>()
        }

        val test = object : BaseJUnitTest(moduleTestingConfiguration) {
            val testUserService by dependency<FakeTestUserService>()
            override fun configure() = super.configure()
                .provide<FakeTestUserService>()
        }

        test.run {
            junitBefore()

            assert(!testUserService.isMock)
        }
    }

    @Test
    fun `provide with lambda overrides global mockOnly configuration`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency mockOnly of<FakeTestUserService>()
        }

        val test = object : BaseJUnitTest(moduleTestingConfiguration) {
            val testUserService by dependency<FakeTestUserService>()
            override fun configure() = super.configure()
                .provide<FakeTestUserService> { FakeTestUserService() }
        }

        test.run {
            junitBefore()

            assert(!testUserService.isMock)
        }
    }

    @Test
    fun `provide without lambda goes hand in hand with global realOnly configuration`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency realOnly of<FakeTestUserService>()
        }

        val test = object : BaseJUnitTest(moduleTestingConfiguration) {
            val testUserService by dependency<FakeTestUserService>()
            override fun configure() = super.configure()
                .provide<FakeTestUserService>()
        }

        test.run {
            junitBefore()

            assert(!testUserService.isMock)
        }
    }

    @Test
    fun `provide with lambda overrides global realOnly configuration`() {
        val moduleTestingConfiguration = moduleTestingConfiguration {
            dependency realOnly of<FakeTestUserService>()
        }

        val test = object : BaseJUnitTest(moduleTestingConfiguration) {
            val testUserService by dependency<FakeTestUserService>()
            override fun configure() = super.configure()
                .provide<FakeTestUserService> { mock() }
        }

        test.run {
            junitBefore()

            assert(testUserService.isMock)
        }
    }
}
