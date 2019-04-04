package com.mysugr.sweetest.v2.api

/**
 * Should be used to abstract interaction with a production dependency class.
 * Has abilities of [Actor] (consume dependencies).
 * Allows multiple instance of its own type.
 * Allows just one live instance of its own type (one dependency per dependency actor).
 */
abstract class DependencyActor : Actor() {

    /**
     * Creates a mock of the given class/interface.
     * In the initializer block you have access to dependency management and can initialize the mock's behaviour.
     * The resulting mock is put under dependency management and will be injected wherever the type is requested.
     * The resulting mock can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> mockDependency(initializeMock: InitializerScope.(mock: T) -> Unit = {}):
            DependencyPropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Lets an object be created by automatically resolving its constructor's dependencies.
     * In the initializer block you have access to dependency management and can do initializations on the instance.
     * The resulting object is put under dependency management and will be injected wherever the type is requested.
     * The resulting object can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> autoDependency(initializeInstance: InitializerScope.(instance: T) -> Unit = {}):
            DependencyPropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Lets an object be created in an instance creation block.
     * In the block you also have access to dependency management.
     * The resulting object is put under dependency management and will be injected wherever the type is requested.
     * The resulting object can be consumed via the delegated property and is acted on in this class.
     * Only one of these objects can be acted on in an [Actor] class.
     */
    fun <T : Any> customDependency(createInstance: InitializerScope.() -> T): DependencyPropertyDelegate<T> {
        throw NotImplementedError()
    }
}
