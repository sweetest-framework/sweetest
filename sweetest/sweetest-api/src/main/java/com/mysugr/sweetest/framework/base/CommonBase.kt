package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.Steps
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class CommonBase(@PublishedApi internal val testContext: TestContext)

// --- region: Published API (necessary to keep inlined footprint as small as possible)

inline fun <reified T : Any> CommonBase.dependency(): DependencyPropertyDelegate<T> = dependency(this, T::class)
inline fun <reified T : BaseSteps> CommonBase.steps(): PropertyDelegate<T> = steps(this, T::class)

class PropertyDelegate<out T>(private val getter: () -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
}

// TODO remove
class DependencyPropertyDelegate<out T : Any>(private val getDependencyState: (() -> DependencyState<T>)) {

    private var cachedDependencyState: DependencyState<T>? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        (cachedDependencyState ?: getDependencyState().also { cachedDependencyState = it }).instance
}

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependency(scope: CommonBase, type: KClass<T>): DependencyPropertyDelegate<T> {
    try {
        return DependencyPropertyDelegate {
            try {
                TestEnvironment.dependencies.getDependencyState(type)
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
internal fun <T : BaseSteps> steps(scope: CommonBase, type: KClass<T>): PropertyDelegate<T> {
    try {
        scope.testContext.steps.setUpAsRequired(type as KClass<Steps>)
        return PropertyDelegate {
            try {
                scope.testContext.steps.get(type) as T
            } catch (throwable: Throwable) {
                throw SweetestException(
                    "Providing steps class instance for \"steps<${type.simpleName}>\" failed",
                    throwable
                )
            }
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"steps<$type>\" failed",
            throwable
        )
    }
}
