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
     * In the initializer block you have access to dependency management and can initialize the mock's behaviour.
     * The resulting mock can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> mock(initializeMock: InitializerScope.(mock: T) -> Unit = {}): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Lets an object be created by automatically resolving its constructor's dependencies.
     * In the initializer block you have access to dependency management and can do initializations on the instance.
     * The resulting object can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> auto(initializeInstance: InitializerScope.(instance: T) -> Unit = {}): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Lets an object be created in an instance creation block.
     * In the block you also have access to dependency management.
     * The resulting object can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> custom(createInstance: InitializerScope.() -> T): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }
}
