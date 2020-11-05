package com.mysugr.sweetest.framework.accessor

import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class DelegatesAccessor(@PublishedApi internal val accessor: BaseAccessor) {

    inline fun <reified T : Steps> steps(): ReadOnlyPropertyDelegate<T> {
        try {
            accessor.testContext.steps.setUpAsRequired(T::class as KClass<Steps>)
            return ReadOnlyPropertyDelegate {
                try {
                    accessor.testContext.steps.get(T::class)
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

    inline fun <reified T : Any> dependency(): DependencyPropertyDelegate<T> {
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

    inline fun <reified T : Any> factory() = try {
        ReadOnlyPropertyDelegate {
            accessor.testContext.factories.get<T>().run(accessor.testContext.steps.provider)
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"factory<${T::class.simpleName}>\" failed",
            throwable
        )
    }

    class ReadOnlyPropertyDelegate<out T>(private val getter: () -> T) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
    }

    class DependencyPropertyDelegate<out T : Any>(private val getDependencyState: (() -> DependencyState<T>)) {

        // For API compatibility
        constructor(dependencyState: DependencyState<T>) : this(getDependencyState = { dependencyState })

        private var cachedDependencyState: DependencyState<T>? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            (cachedDependencyState ?: getDependencyState().also { cachedDependencyState = it }).instance
    }
}
