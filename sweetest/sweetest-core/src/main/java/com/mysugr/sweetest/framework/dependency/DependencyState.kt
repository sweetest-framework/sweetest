package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.DependencyProvider
import com.mysugr.sweetest.internal.DependencyProviderArgumentProvider
import org.mockito.Mockito
import org.mockito.exceptions.base.MockitoException
import kotlin.reflect.KClass

internal class DependencyState<T : Any>(
    private val dependencyProviderArgumentProvider: DependencyProviderArgumentProvider,
    val configuration: DependencyConfiguration<T>,
    initializer: DependencyProvider<T>? = null,
    mode: DependencyMode? = null
) {

    private var instanceField: T? = null
    private var modeField: DependencyMode? = mode

    var providedInitializer: DependencyProvider<T>? = null
    var providedInitializerUnknown: DependencyProvider<*>?
        set(value) {
            providedInitializer = value as? DependencyProvider<T>
        }
        get() = providedInitializer

    var realInitializer: DependencyProvider<T>? = initializer
        ?: configuration.defaultRealInitializer

    var realInitializerUnknown: DependencyProvider<*>?
        set(value) {
            realInitializer = value as? DependencyProvider<T>
        }
        get() = realInitializer

    var mockInitializer: DependencyProvider<T>? = initializer
        ?: configuration.defaultMockInitializer

    var mockInitializerUnknown: DependencyProvider<*>?
        set(value) {
            mockInitializer = value as? DependencyProvider<T>
        }
        get() = mockInitializer

    val instance: T
        get() = instanceField ?: initializeInstance()

    var mode: DependencyMode
        get() = modeField ?: configuration.defaultDependencyMode ?: DependencyMode.MOCK
        set(value) {
            when {
                value == modeField -> {
                    return
                }
                modeField != null -> {
                    throw SweetestException(
                        "Dependency \"${configuration.clazz.simpleName}\" can't be configured to be " +
                            "${getModeDescription(value)}: it has already been configured to be " +
                            "${getModeDescription(modeField)}."
                    )
                }
                instanceField != null -> {
                    throw SweetestException(
                        "Can't set dependency mode of \"${configuration.clazz.simpleName}\": instance " +
                            "has already been created."
                    )
                }
                else -> {
                    modeField = value
                }
            }
        }

    private fun getModeDescription(mode: DependencyMode? = null): String {
        return when (mode) {
            DependencyMode.SPY -> "spy (`requireSpy<${configuration.clazz.simpleName}>()`)"
            DependencyMode.AUTO_PROVIDED -> "provided (`provide<${configuration.clazz.simpleName}>()`)"
            DependencyMode.PROVIDED -> "provided (`provide<${configuration.clazz.simpleName}> { ... }`)"
            DependencyMode.REAL -> "real (`requireReal`, `realOnly`, etc.)"
            DependencyMode.MOCK -> "mock (`requireMock`, `mockOnly`, etc.)"
            null -> "not set"
        }
    }

    private fun initializeInstance(): T {
        val instance = when (mode) {
            DependencyMode.REAL, DependencyMode.AUTO_PROVIDED -> createInstance()
            DependencyMode.MOCK -> createMock()
            DependencyMode.SPY -> Mockito.spy(createInstance())
            DependencyMode.PROVIDED -> createProvidedInstance()
        }
        this.instanceField = instance
        return instance
    }

    private fun createMock(): T =
        mockInitializer?.let { it(dependencyProviderArgumentProvider()) } ?: createDefaultMock()

    private fun createDefaultMock(): T = Mockito.mock(configuration.clazz.java)

    private fun createInstance(): T {
        return realInitializer?.let {
            createInstanceBy(it)
        } ?: createInstanceAutomatically()
    }

    private fun createInstanceAutomatically(): T {
        return try {
            val constructors = configuration.clazz.constructors
            if (configuration.clazz.isAbstract) {
                throw IllegalArgumentException(
                    "Dependencies like \"${configuration.clazz.simpleName}\" which are abstract can not be " +
                        "auto-initialized. Please define how to instantiate it by adding a `provide { ... }` " +
                        "configuration!"
                )
            }
            if (constructors.size > 1) {
                throw IllegalArgumentException(
                    "Dependencies like \"${configuration.clazz.simpleName}\" which have more than one constructor " +
                        "can't be auto-initialized. Please define how to instantiate it by adding a " +
                        "`provide { ... }` configuration!"
                )
            }
            val constructor = constructors.first()
            val argumentTypes = constructor.parameters.map { it.type.classifier as KClass<*> }

            val arguments = try {
                argumentTypes.map { TestEnvironment.dependencies.getDependencyState(it).instance }.toTypedArray()
            } catch (exception: Exception) {
                throw RuntimeException(
                    "At least one dependency required by the constructor could " +
                        "not be found.",
                    exception
                )
            }

            constructor.call(*arguments)
        } catch (exception: Exception) {
            throw RuntimeException(
                "Couldn't automatically construct dependency \"$configuration\". Either you need " +
                    "a manual initializer, the class should have a single constructor or one of the dependencies " +
                    "required by the constructor could not be initialized.",
                exception
            )
        }
    }

    private fun createProvidedInstance(): T {
        return providedInitializer?.let {
            createInstanceBy(it)
        } ?: throw RuntimeException(
            "Cannot create provided instance, because providedInitializer is not set."
        )
    }

    private fun createInstanceBy(initializer: DependencyProvider<T>): T {
        return try {
            initializer(dependencyProviderArgumentProvider())
        } catch (dependencyException: DependencyInstanceInitializationException) {
            throw dependencyException
        } catch (mockitoException: MockitoException) {
            throw mockitoException
        } catch (throwable: Throwable) {
            throw DependencyInstanceInitializationException("Initializer for \"$configuration\" failed", throwable)
        }
    }

    class DependencyInstanceInitializationException(message: String, cause: Throwable) :
        Exception(message, cause)
}
