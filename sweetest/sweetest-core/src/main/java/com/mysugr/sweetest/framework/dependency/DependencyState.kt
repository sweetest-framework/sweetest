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
    provider: DependencyProvider<T>? = null,
    mode: DependencyMode? = null
) {

    private var instanceField: T? = null
    private var modeField: DependencyMode? = mode

    var provider: DependencyProvider<T>? = null
    var providerUnknown: DependencyProvider<*>?
        set(value) {
            provider = value as? DependencyProvider<T>
        }
        get() = provider

    var realProvider: DependencyProvider<T>? = provider
        ?: configuration.defaultRealProvider

    var realProviderUnknown: DependencyProvider<*>?
        set(value) {
            realProvider = value as? DependencyProvider<T>
        }
        get() = realProvider

    var mockProvider: DependencyProvider<T>? = provider
        ?: configuration.defaultMockProvider

    var mockProviderUnknown: DependencyProvider<*>?
        set(value) {
            mockProvider = value as? DependencyProvider<T>
        }
        get() = mockProvider

    val instance: T
        get() = instanceField ?: provideInstance()

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

    private fun provideInstance(): T {
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
        mockProvider?.let { it(dependencyProviderArgumentProvider()) } ?: createDefaultMock()

    private fun createDefaultMock(): T = Mockito.mock(configuration.clazz.java)

    private fun createInstance(): T {
        return realProvider?.let {
            createInstanceBy(it)
        } ?: createInstanceAutomatically()
    }

    private fun createInstanceAutomatically(): T {
        return try {
            val constructors = configuration.clazz.constructors
            if (configuration.clazz.isAbstract) {
                throw IllegalArgumentException(
                    "Dependencies like \"${configuration.clazz.simpleName}\" which are abstract can not be " +
                        "auto-provided. Please define how to instantiate it by adding a " +
                        "`provide<${configuration.clazz.simpleName}> { ... }` configuration!"
                )
            }
            if (constructors.size > 1) {
                throw IllegalArgumentException(
                    "Dependencies like \"${configuration.clazz.simpleName}\" which have more than one constructor " +
                        "can't be auto-provided. Please define how to instantiate it by adding a " +
                        "`provide<${configuration.clazz.simpleName}> { ... }` configuration!"
                )
            }
            val constructor = constructors.first()
            val argumentTypes = constructor.parameters.map { it.type.classifier as KClass<*> }

            val arguments = try {
                argumentTypes.map { TestEnvironment.dependencies.getDependencyState(it).instance }.toTypedArray()
            } catch (exception: Exception) {
                throw RuntimeException(
                    "At least one dependency required by the constructor could not be found.",
                    exception
                )
            }

            constructor.call(*arguments)
        } catch (exception: Exception) {
            throw RuntimeException(
                "Couldn't automatically construct dependency \"$configuration\". Either you need " +
                    "a manual provider, the class should have a single constructor or one of the dependencies " +
                    "required by the constructor could not be initialized.",
                exception
            )
        }
    }

    private fun createProvidedInstance(): T {
        return provider?.let {
            createInstanceBy(it)
        } ?: throw RuntimeException(
            "Cannot create provided instance, because `provider` is not set."
        )
    }

    private fun createInstanceBy(provider: DependencyProvider<T>): T {
        return try {
            provider(dependencyProviderArgumentProvider())
        } catch (dependencyException: DependencyInstanceInitializationException) {
            throw dependencyException
        } catch (mockitoException: MockitoException) {
            throw mockitoException
        } catch (throwable: Throwable) {
            throw DependencyInstanceInitializationException("Provider for \"$configuration\" failed", throwable)
        }
    }

    class DependencyInstanceInitializationException(message: String, cause: Throwable) :
        Exception(message, cause)
}
