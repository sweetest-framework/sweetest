/**
 * Use cases for dependencies.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 *
 * Note: DependenciesTestContext is mentioned in some function signatures while it's not used in the function itself.
 * This is to prepare for the upcoming removal of the global state (TestEnvironment).
 */

package dev.sweetest.internal.dependency

import dev.sweetest.internal.SweetestException
import dev.sweetest.internal.InternalBaseTestElement
import dev.sweetest.internal.InternalSweetestApi
import dev.sweetest.internal.environment.TestEnvironment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private const val DEPRECATION_MESSAGE = "Legacy dependency modes."

// --- region: Configuration

@InternalSweetestApi
fun <T : Any> configureDependencyProvision(
    dependenciesTestContext: DependenciesTestContext,
    dependencyType: KClass<T>,
    provider: DependencyProvider<T>
) {
    dependenciesTestContext.editDependencyState(dependencyType) {
        dependenciesTestContext.checkNotAlreadyProvided(dependencyType, mode)
        mode = DependencyMode.PROVIDED
        providerUnknown = provider
    }
}

@InternalSweetestApi
fun <T : Any> configureDependencyProvisionAutomatic(
    dependenciesTestContext: DependenciesTestContext,
    dependencyType: KClass<T>
) {
    dependenciesTestContext.editDependencyState(dependencyType) {
        dependenciesTestContext.checkNotAlreadyProvided(dependencyType, mode)
        mode = DependencyMode.AUTO_PROVIDED
    }
}

@InternalSweetestApi
@Deprecated(DEPRECATION_MESSAGE)
fun configureDependencyReal(
    dependenciesTestContext: DependenciesTestContext,
    dependencyType: KClass<*>,
    forceMode: Boolean = false,
    offerProvider: DependencyProvider<*>? = null
) = dependenciesTestContext.editLegacyDependencyState(dependencyType) {
    if (forceMode) {
        mode = DependencyMode.REAL
    }
    if (offerProvider != null) {
        realProviderUnknown = offerProvider
    }
}

@InternalSweetestApi
@Deprecated(DEPRECATION_MESSAGE)
fun configureDependencyMock(
    dependenciesTestContext: DependenciesTestContext,
    dependencyType: KClass<*>,
    forceMode: Boolean = false,
    offerProvider: DependencyProvider<*>? = null
) = dependenciesTestContext.editLegacyDependencyState(dependencyType) {
    if (forceMode) {
        mode = DependencyMode.MOCK
    }
    if (offerProvider != null) {
        mockProviderUnknown = offerProvider
    }
}

@InternalSweetestApi
@Deprecated(DEPRECATION_MESSAGE)
fun configureDependencySpy(dependenciesTestContext: DependenciesTestContext, dependencyType: KClass<*>) =
    dependenciesTestContext.editLegacyDependencyState(dependencyType) {
        mode = DependencyMode.SPY
    }

// --- region: Consumption

@InternalSweetestApi
fun <T : Any> getDependencyInstance(
    @Suppress("UNUSED_PARAMETER")
    dependenciesTestContext: DependenciesTestContext, // reserved for API stability
    dependencyType: KClass<T>
): T {
    return TestEnvironment.dependencies.getDependencyState(dependencyType).instance
}

@InternalSweetestApi
fun <T : Any> getDependencyDelegate(
    @Suppress("UNUSED_PARAMETER")
    dependenciesTestContext: DependenciesTestContext, // reserved for API stability
    type: KClass<T>
): ReadOnlyProperty<InternalBaseTestElement, T> {
    try {
        var cachedDependencyState: DependencyState<T>? = null
        return object : ReadOnlyProperty<InternalBaseTestElement, T> {
            override fun getValue(thisRef: InternalBaseTestElement, property: KProperty<*>): T {
                return try {
                    if (cachedDependencyState == null) {
                        cachedDependencyState = TestEnvironment.dependencies.getDependencyState(type)
                    }
                    cachedDependencyState!!.instance
                } catch (throwable: Throwable) {
                    throw SweetestException(
                        "Providing dependency for \"dependency<${type.simpleName}>\" failed",
                        throwable
                    )
                }
            }
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"dependency<${type.simpleName}>\" failed",
            throwable
        )
    }
}
