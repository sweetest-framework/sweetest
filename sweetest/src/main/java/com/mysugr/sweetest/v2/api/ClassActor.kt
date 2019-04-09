package com.mysugr.sweetest.v2.api

/**
 * Should be used to abstract interaction with a production class.
 * Has abilities of [Actor] (consume dependencies).
 * Allows multiple instance of its own type.
 * Allows for putting this instance as [mock], [spy], or real instance (by the use of [custom] or [auto]).
 * Allows just one live instance of its own type (one object per dependency actor).
 */
abstract class ClassActor : Actor() {

    /**
     * Creates a mock of the given class/interface.
     * In the optional block you should initialize the mock's behaviour.
     * The resulting mock can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> mock(initializeMock: (mock: T) -> Unit = {}): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Lets an object be created by automatically resolving its constructor's dependencies.
     * In the optional block you should set up on the instance.
     * The resulting object can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> auto(setUpInstance: (instance: T) -> Unit = {}): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Lets an object be created in a custom way.
     * In the block you should offer instantiation logic.
     * In the block you also have access to dependency management in case you need other dependencies.
     * The resulting object can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> custom(createInstance: InstantiationScope.() -> T): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }
}
