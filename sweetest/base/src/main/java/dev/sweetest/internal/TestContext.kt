package dev.sweetest.internal

/**
 * Although `TestContext` is an internal data structure it is not in the internal package because it was used publicly
 * in the v1 API. From v2 on `TestContext` is only used internally.
 *
 * API v1 doesn't require the use of `TestContext` directly, so it is now considered _internal_.
 *
 * TestContext can hold an arbitrary number of [TestContextElement]s. It creates an instance if it has not already.
 * There can only be one instance per type. [TestContextElement.Key] is here for type-safe, reflection-free
 * instantiation of a specific subtype of [TestContextElement].
 */

/**
 * Holds the utilities and state of a test. Is not meant to be used publicly from sweetest v2 on!
 */
class TestContext {

    private val elements = mutableMapOf<TestContextElement.Definition<*>, TestContextElement>()

    operator fun <T : TestContextElement> get(definition: TestContextElement.Definition<T>): T {
        @Suppress("UNCHECKED_CAST")
        return elements.getOrPut(definition) { definition.createInstance(this) } as T
    }
}

/**
 * Each element stored in [TestContext] needs to implement this interface.
 */
interface TestContextElement {

    /**
     * Needs to return `Companion` that implements [Definition].
     */
    val definition: Definition<*>

    /**
     * Needs to be implemented by the companion object of the [TestContextElement] and create an implementation
     * instance of the [TestContextElement] subclass. The [elementProvider] can be used to get other instances of
     * [TestContextElement], so this emulates a simple kind of dependency management for [TestContextElement]s.
     */
    interface Definition<T> {
        fun createInstance(testContext: TestContext): T
    }
}
