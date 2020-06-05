package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.environment.TestEnvironment
import org.junit.After
import org.junit.Ignore
import org.junit.Test

class TypeMatchingTest {

    open class A
    open class B : A()
    class C : B()

    val config by lazy {
        moduleTestingConfiguration {
            dependency mockOnly initializer { B() }
        }
    }

    @After
    fun tearDown() {
        TestEnvironment.fullReset()
    }

    @Test
    fun `Can get B by retrieving A`() {
        class TestClass : BaseJUnitTest(config) {
            val instance by dependency<A>()
        }

        val test = TestClass()
        test.junitBefore()
        test.instance
    }

    @Test
    fun `Can configure A locally although B is configured globally`() {
        class TestClass : BaseJUnitTest(config) {
            override fun configure() = super.configure()
                .requireMock<A>()
        }

        val test = TestClass()
        test.junitBefore()
    }

    @Test
    fun `Can provide C, consuming A`() {
        class TestClass : BaseJUnitTest(config) {
            val instance by dependency<A>()
            override fun configure() = super.configure()
                .offerMockRequired<B> { C() }
        }

        val test = TestClass()
        test.junitBefore()
        assert(test.instance::class == C::class)
    }

    @Test
    fun `Can provide C, consuming C`() {
        class TestClass : BaseJUnitTest(config) {
            val instance by dependency<C>()
            override fun configure() = super.configure()
                .offerMockRequired<B> { C() }
        }
        val test = TestClass()
        test.junitBefore()
        assert(test.instance::class == C::class)
    }

    @Test
    @Ignore("Global dependency configuration is going to be deprecated, Not supported for backwards-compatibility reasons and as ")
    fun `Can configure B globally which provides C`() {
        val config = moduleTestingConfiguration {
            dependency mockOnly initializer<B> { C() }
        }
        class TestClass : BaseJUnitTest(config) {
            val instance by dependency<C>()
        }
        val test = TestClass()
        test.junitBefore()
        assert(test.instance::class == C::class)
    }

    // TODO add tests that reflect the current "unprecise" type matching, too
}