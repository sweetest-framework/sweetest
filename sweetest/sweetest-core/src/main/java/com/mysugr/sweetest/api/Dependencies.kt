package com.mysugr.sweetest.api

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.internal.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.CommonBase
import com.mysugr.sweetest.util.PropertyDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

fun <T : Any> configureDependencyProvision(
    dependenciesTestContext: DependenciesTestContext,
    type: KClass<T>,
    initializer: DependencyInitializer<T>
) {
    dependenciesTestContext.provide(type, initializer)
}

fun <T : Any> configureAutomaticDependencyProvision(
    dependenciesTestContext: DependenciesTestContext,
    type: KClass<T>
) {
    dependenciesTestContext.provide(type)
}

fun <T : Any> getDependencyDelegate(
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
