package dev.sweetest.api.v2.framework

import dev.sweetest.api.v2.framework.base.JUnit4Test
import com.mysugr.sweetest.framework.base.SweetestException
import org.junit.Test

class ProvideTest : BaseTest() {

    interface TestUserService

    class FakeTestUserService : TestUserService

    class TestViewModel(internal val userService: TestUserService)

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (once with, once without lambda)`() {
        class TestClass : JUnit4Test() {
            override fun configure() = super.configure()
                .provide { TestViewModel(object : TestUserService {}) }
                .provide<TestViewModel>()
        }

        TestClass().run {
            junitBefore()
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (once without, once with lambda)`() {
        class TestClass : JUnit4Test() {
            override fun configure() = super.configure()
                .provide<TestViewModel>()
                .provide { TestViewModel(object : TestUserService {}) }
        }

        TestClass().run {
            junitBefore()
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (with lambda)`() {
        class TestClass : JUnit4Test() {
            override fun configure() = super.configure()
                .provide { TestViewModel(object : TestUserService {}) }
                .provide { TestViewModel(object : TestUserService {}) }
        }

        TestClass().run {
            junitBefore()
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (without lambda)`() {
        class TestClass : JUnit4Test() {
            override fun configure() = super.configure()
                .provide<TestViewModel>()
                .provide<TestViewModel>()
        }

        TestClass().run {
            junitBefore()
        }
    }

    // TODO more tests
}
