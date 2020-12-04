package dev.sweetest.v2

import com.mysugr.sweetest.framework.base.SweetestException
import dev.sweetest.api.v2.BaseTest
import org.junit.Test

class ProvideTest : AutoWipeTest() {

    interface TestUserService

    class FakeTestUserService : TestUserService

    class TestViewModel(internal val userService: TestUserService)

    @Test
    fun `Simple provide usage`() {
        val testInstance = FakeTestUserService()

        class TestClass : BaseTest() {
            init {
                provide { testInstance }
            }

            val instance by dependency<FakeTestUserService>()
        }

        with(TestClass()) {
            try {
                startWorkflow()
                assert(instance === testInstance)
            } finally {
                finishWorkflow()
            }
        }
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (once with, once without lambda)`() {
        class TestClass : BaseTest() {
            init {
                provide {
                    TestViewModel(object :
                        TestUserService {})
                }
                provide<TestViewModel>()
            }
        }

        TestClass().startWorkflow()
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (once without, once with lambda)`() {
        class TestClass : BaseTest() {
            init {
                provide<TestViewModel>()
                provide {
                    TestViewModel(object :
                        TestUserService {})
                }
            }
        }

        TestClass().startWorkflow()
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (with lambda)`() {
        class TestClass : BaseTest() {
            init {
                provide {
                    TestViewModel(object :
                        TestUserService {})
                }
                provide {
                    TestViewModel(object :
                        TestUserService {})
                }
            }
        }

        TestClass().startWorkflow()
    }

    @Test(expected = SweetestException::class)
    fun `provide cannot be used twice for the same type (without lambda)`() {
        class TestClass : BaseTest() {
            init {
                provide<TestViewModel>()
                provide<TestViewModel>()
            }
        }

        TestClass().startWorkflow()
    }
}
