package com.mysugr.sweetest.usecases

/**
 * Use cases for dependencies.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 *
 * Note: DependenciesTestContext is mentioned in some function signatures while it's not used in the function itself.
 * This is to prepare for the upcoming removal of the global state (TestEnvironment).
 */

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.internal.DependencyProvider
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.CommonBase
import com.mysugr.sweetest.internal.DependencyProviderArgument
import com.mysugr.sweetest.util.PropertyDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

private const val DEPRECATION_MESSAGE = "Legacy dependency modes"

// --- region: Initialization

fun initializeDependencies(
    dependenciesTestContext: DependenciesTestContext,
    dependencyProviderArgument: DependencyProviderArgument
) {
    TestEnvironment.initializeDependencies(dependencyProviderArgument)
}

// --- region: Configuration

fun <T : Any> configureDependencyProvision(
    dependenciesTestContext: DependenciesTestContext,
    type: KClass<T>,
    initializer: DependencyProvider<T>
) {
    dependenciesTestContext.editDependencyState(type) {
        dependenciesTestContext.checkNotAlreadyProvided(type, mode)
        mode = DependencyMode.PROVIDED
        providedInitializerUnknown = initializer
    }
}

fun <T : Any> configureDependencyProvisionAutomatic(dependenciesTestContext: DependenciesTestContext, type: KClass<T>) {
    dependenciesTestContext.editDependencyState(type) {
        dependenciesTestContext.checkNotAlreadyProvided(type, mode)
        mode = DependencyMode.AUTO_PROVIDED
    }
}

@Deprecated(DEPRECATION_MESSAGE)
fun configureDependencyReal(
    dependenciesTestContext: DependenciesTestContext,
    type: KClass<*>,
    forceMode: Boolean = false,
    offerProvider: DependencyProvider<*>? = null
) =
    dependenciesTestContext.editLegacyDependencyState(type) {
        if (forceMode) {
            mode = DependencyMode.REAL
        }
        if (offerProvider != null) {
            realInitializerUnknown = offerProvider
        }
    }

@Deprecated(DEPRECATION_MESSAGE)
fun configureDependencyMock(
    dependenciesTestContext: DependenciesTestContext,
    type: KClass<*>,
    forceMode: Boolean = false,
    offerProvider: DependencyProvider<*>? = null
) = dependenciesTestContext.editLegacyDependencyState(type) {
    if (forceMode) {
        mode = DependencyMode.MOCK
    }
    if (offerProvider != null) {
        mockInitializerUnknown = offerProvider
    }
}

@Deprecated(DEPRECATION_MESSAGE)
fun configureDependencySpy(dependenciesTestContext: DependenciesTestContext, type: KClass<*>) =
    dependenciesTestContext.editLegacyDependencyState(type) {
        mode = DependencyMode.SPY
    }

// --- region: Consumption

fun <T : Any> getDependencyInstance(dependenciesTestContext: DependenciesTestContext, type: KClass<T>): T {
    return TestEnvironment.dependencies.getDependencyState(type).instance
}

fun <T : Any> getDependencyDelegate(
    dependenciesTestContext: DependenciesTestContext,
    type: KClass<T>
): ReadOnlyProperty<CommonBase, T> {
    try {
        var cachedDependencyState: DependencyState<T>? = null
        return PropertyDelegate {
            try {
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
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"dependency<${type.simpleName}>\" failed",
            throwable
        )
    }
}
