package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.Steps
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class CommonBase(@PublishedApi internal val testContext: TestContext)

inline fun <reified T : Any> CommonBase.dependency(): DependencyPropertyDelegate<T> {
    try {
        return DependencyPropertyDelegate {
            try {
                TestEnvironment.dependencies.getDependencyState(T::class)
            } catch (throwable: Throwable) {
                throw SweetestException(
                    "Providing dependency for \"dependency<${T::class.simpleName}>\" failed",
                    throwable
                )
            }
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"dependency<${T::class.simpleName}>\" failed",
            throwable
        )
    }
}

inline fun <reified T : BaseSteps> CommonBase.steps(): PropertyDelegate<T> {
    try {
        testContext.steps.setUpAsRequired(T::class as KClass<Steps>)
        return PropertyDelegate {
            try {
                testContext.steps.get(T::class)
            } catch (throwable: Throwable) {
                throw SweetestException(
                    "Providing steps class instance for \"steps<${T::class.simpleName}>\" failed",
                    throwable
                )
            }
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"steps<${T::class.simpleName}>\" failed",
            throwable
        )
    }
}

inline fun <reified T : Any> CommonBase.factory(): PropertyDelegate<T> {
    try {
        return PropertyDelegate {
            testContext.factories.get<T>().run(testContext.steps.provider)
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"factory<${T::class.simpleName}>\" failed",
            throwable
        )
    }
}

class PropertyDelegate<out T>(private val getter: () -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
}

class DependencyPropertyDelegate<out T : Any>(private val getDependencyState: (() -> DependencyState<T>)) {

    private var cachedDependencyState: DependencyState<T>? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        (cachedDependencyState ?: getDependencyState().also { cachedDependencyState = it }).instance
}
