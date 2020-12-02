package com.mysugr.sweetest

import org.junit.Test

class TestContextTest {

    /**
     * For testing purposes there are three [TestContextElement]s which depend on each other
     *
     * ```
     *    Element1   <--  Element2
     *    Element1   <--  Element3
     *    Element2   <--  Element3
     * ```
     *
     * An element can exist just once. The elements provided via [TestContext] have to be the same
     * instance as provided before by the [TestContext].
     */
    @Test
    fun `TestContext provides correctly initialized types`() {
        val testContext = TestContext()

        val element2 = testContext[SomeTestContextElement2]
        assert(element2 is SomeTestContextElement2)
        assert(element2.key === SomeTestContextElement2)

        val element1From2 = element2.element1
        assert(element1From2 is SomeTestContextElement1)

        val element1 = testContext[SomeTestContextElement1]
        assert(element1 === element1From2)
        assert(element1 is SomeTestContextElement1)
        assert(element1.key === SomeTestContextElement1)

        val element3 = testContext[SomeTestContextElement3]
        assert(element3 is SomeTestContextElement3)
        assert(element3.key === SomeTestContextElement3)

        val element1From3 = element3.element1
        assert(element1From3 === element1)

        val element2From3 = element3.element2
        assert(element2From3 === element2)
    }

    private class SomeTestContextElement1 : TestContextElement {

        override val key: TestContextElement.Key<*> = Key

        companion object Key : TestContextElement.Key<SomeTestContextElement1> {
            override fun createInstance(testContext: TestContext): SomeTestContextElement1 {
                return SomeTestContextElement1()
            }
        }
    }

    private class SomeTestContextElement2(val element1: SomeTestContextElement1) : TestContextElement {

        override val key: TestContextElement.Key<*> = Key

        companion object Key : TestContextElement.Key<SomeTestContextElement2> {
            override fun createInstance(testContext: TestContext): SomeTestContextElement2 {
                return SomeTestContextElement2(testContext[SomeTestContextElement1])
            }
        }
    }

    private class SomeTestContextElement3(
        val element1: SomeTestContextElement1,
        val element2: SomeTestContextElement2
    ) : TestContextElement {

        override val key: TestContextElement.Key<*> = Key

        companion object Key : TestContextElement.Key<SomeTestContextElement3> {
            override fun createInstance(testContext: TestContext): SomeTestContextElement3 {
                return SomeTestContextElement3(
                    testContext[SomeTestContextElement1],
                    testContext[SomeTestContextElement2]
                )
            }
        }
    }
}