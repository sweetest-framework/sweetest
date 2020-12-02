package com.mysugr.sweetest

/**
 * TestContext can hold an arbitrary number of [TestContextElement]s. It creates an instance if it has not already.
 * There can only be one instance per type. [TestContextElement.Key] is here for type-safe, reflection-free
 * instantiation of a specific subtype of [TestContextElement].
 */
class TestContext : TestContextElementProvider {

    private val elements = mutableMapOf<TestContextElement.Key<*>, TestContextElement>()

    override operator fun <T : TestContextElement> get(key: TestContextElement.Key<T>): T {
        @Suppress("UNCHECKED_CAST")
        return elements.getOrPut(key) { key.createInstance(this) } as T
    }
}

interface TestContextElement {

    /**
     * Needs to return the companion object. For that sake the companion
     * object can be named `Key` and returned by this property.
     */
    val key: Key<*>

    /**
     * Needs to be implemented by the companion object of the [TestContextElement] and create an implementation
     * instance of the [TestContextElement] subclass. The [elementProvider] can be used to get other instances of
     * [TestContextElement], so this emulates a simple kind of dependency management for [TestContextElement]s.
     */
    interface Key<T> {
        fun createInstance(elementProvider: TestContextElementProvider): T
    }
}

interface TestContextElementProvider {
    operator fun <T : TestContextElement> get(key: TestContextElement.Key<T>): T
}
