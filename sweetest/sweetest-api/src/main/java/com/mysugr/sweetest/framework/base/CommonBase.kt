package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.api.getStepsFinal
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class CommonBase(@PublishedApi internal val testContext: TestContext) : com.mysugr.sweetest.internal.CommonBase

// --- region: Published API (necessary to keep inlined footprint as small as possible)

inline fun <reified T : Any> CommonBase.dependency(): PropertyDelegate<T> = dependency(this, T::class)
inline fun <reified T : BaseSteps> CommonBase.steps(): ReadOnlyProperty<CommonBase, T> = steps(this, T::class)

class PropertyDelegate<out T>(private val getter: () -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
}

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependency(scope: CommonBase, type: KClass<T>): PropertyDelegate<T> {
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

@PublishedApi
internal fun <T : BaseSteps> steps(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getStepsFinal(scope.testContext.steps, type)