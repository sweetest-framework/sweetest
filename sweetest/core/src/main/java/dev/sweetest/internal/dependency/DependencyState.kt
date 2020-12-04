package dev.sweetest.internal.dependency

import dev.sweetest.internal.SweetestException
import dev.sweetest.internal.DependencyProviderScope
import dev.sweetest.internal.environment.TestEnvironment
import org.mockito.Mockito
import org.mockito.exceptions.base.MockitoException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal class DependencyState<T : Any>(
    private val dependencyProviderScope: DependencyProviderScope,
    val configuration: DependencyConfiguration<T>
) {

    private var instanceField: T? = null
    private var modeField: DependencyMode? = null

    var provider: DependencyProvider<T>? = null
    var providerUnknown: DependencyProvider<*>?
        set(value) {
            @Suppress("UNCHECKED_CAST")
            provider = value as? DependencyProvider<T>
        }
        get() = provider

    var realProvider: DependencyProvider<T>? = configuration.defaultRealProvider

    var realProviderUnknown: DependencyProvider<*>?
        set(value) {
            @Suppress("UNCHECKED_CAST")
            realProvider = value as? DependencyProvider<T>
        }
        get() = realProvider

    var mockProvider: DependencyProvider<T>? = configuration.defaultMockProvider

    var mockProviderUnknown: DependencyProvider<*>?
        set(value) {
            @Suppress("UNCHECKED_CAST")
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
        mockProvider?.let { it(dependencyProviderScope) } ?: createDefaultMock()

    private fun createDefaultMock(): T = Mockito.mock(configuration.clazz.java)

    private fun createInstance(): T {
        return realProvider?.let {
            createInstanceBy(it)
        } ?: createInstanceAutomatically()
    }

    private fun createInstanceAutomatically(): T {
        return try {
            val constructor = getConstructor()
            val constructorArguments = getConstructorArguments(constructor)
            constructor.call(*constructorArguments)
        } catch (exception: Exception) {
            throw RuntimeException(
                "Couldn't automatically construct instance of dependency \"$configuration\"",
                exception
            )
        }
    }

    private fun getConstructorArguments(constructor: KFunction<T>): Array<Any> {
        return constructor.getParameterTypes()
            .map(::getSubDependencyInstanceOfType)
            .toTypedArray()
    }

    private fun KFunction<T>.getParameterTypes() =
        this.parameters.map { it.type.classifier as KClass<*> }

    private fun getConstructor(): KFunction<T> {
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
        return constructor
    }

    private fun getSubDependencyInstanceOfType(dependencyType: KClass<*>): Any {
        return try {
            TestEnvironment.dependencies.getDependencyState(dependencyType).instance
        } catch (exception: Exception) {
            throw RuntimeException(
                "The constructor of \"${configuration.clazz.simpleName}\" needs an instance of dependency " +
                    "\"${dependencyType.simpleName}\", but but it could not be retrieved.",
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
            provider(dependencyProviderScope)
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
