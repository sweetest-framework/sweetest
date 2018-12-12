package com.mysugr.sweetest.framework.dependency

import org.mockito.Mockito
import org.mockito.exceptions.base.MockitoException
import kotlin.reflect.KClass

class DependencyState<T : Any>(
        private val initializerContext: DependencyInitializerContext,
        val configuration: DependencyConfiguration<T>,
        initializer: DependencyInitializer<T>? = null,
        mode: DependencyMode? = null
) {

    private var instanceField: T? = null
    private var modeField: DependencyMode? = mode

    var realInitializer: DependencyInitializer<T>? = initializer
        ?: configuration.defaultRealInitializer

    var realInitializerUnknown: DependencyInitializer<*>?
        set(value) {
            realInitializer = value as? DependencyInitializer<T>
        }
        get() = realInitializer

    var mockInitializer: DependencyInitializer<T>? = initializer
        ?: configuration.defaultMockInitializer

    var mockInitializerUnknown: DependencyInitializer<*>?
        set(value) {
            mockInitializer = value as? DependencyInitializer<T>
        }
        get() = mockInitializer

    val instance: T
        get() = instanceField ?: initializeInstance()

    var mode: DependencyMode
        get() = modeField ?: configuration.defaultDependencyMode ?: DependencyMode.MOCK
        set(value) {
            if (value != modeField) {
                if (modeField != null) {
                    throw IllegalStateException("Can't change dependency mode or \"${configuration.clazz}\", it " +
                        "has already been set before and can't be changed afterwards")
                }
                if (instanceField != null) {
                    throw IllegalStateException("Can't set dependency mode of \"${configuration.clazz}\", instance " +
                        "has already been created")
                }
                modeField = value
            }
        }

    private fun initializeInstance(): T {
        val instance = when (mode) {
            DependencyMode.REAL -> createInstance()
            DependencyMode.MOCK -> createMock()
            DependencyMode.SPY -> Mockito.spy(configuration.clazz.java)
        }
        this.instanceField = instance
        return instance
    }

    private fun createMock(): T = mockInitializer?.let { it(initializerContext) } ?: createDefaultMock()

    private fun createDefaultMock(): T = Mockito.mock(configuration.clazz.java)

    private fun createInstance(): T {
        return realInitializer?.let {
            createInstanceBy(it)
        } ?: createInstanceAutomatically()
    }

    private fun createInstanceAutomatically(): T {
        return try {
            val constructors = configuration.clazz.constructors
            if (constructors.size > 1) {
                throw IllegalArgumentException("Can't auto-initialize dependency which has more than one constructor")
            }
            val constructor = constructors.first()
            val argumentTypes = constructor.parameters.map { it.type.classifier as KClass<*> }

            val arguments = try {
                argumentTypes.map { initializerContext.instanceOf(it) }.toTypedArray()
            } catch (exception: Exception) {
                throw RuntimeException("At least one dependency required by the constructor could " +
                    "not be found.", exception)
            }

            constructor.call(*arguments)
        } catch (exception: Exception) {
            throw RuntimeException("Couldn't automatically construct dependency \"$configuration\". Either you need " +
                "a manual initializer, the class should have a single constructor or one of the dependencies " +
                "required by the constructor could not be initialized.", exception)
        }
    }

    private fun createInstanceBy(initializer: DependencyInitializer<T>): T {
        return try {
            initializer(initializerContext)
        } catch (dependencyException: DependencyInstanceInitializationException) {
            throw dependencyException
        } catch (mockitoException: MockitoException) {
            throw mockitoException
        } catch (throwable: Throwable) {
            throw DependencyInstanceInitializationException("Initializer for \"$configuration\" " +
                "failed", throwable)
        }
    }

    class DependencyInstanceInitializationException(message: String, cause: Throwable)
        : Exception(message, cause)
}
