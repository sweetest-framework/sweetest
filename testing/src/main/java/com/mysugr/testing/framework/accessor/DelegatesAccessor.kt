package com.mysugr.testing.framework.accessor

import com.mysugr.testing.framework.base.BaseSteps
import com.mysugr.testing.framework.base.Steps
import com.mysugr.testing.framework.dependency.DependencyState
import com.mysugr.testing.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

class DelegatesAccessor(@PublishedApi internal val accessor: BaseAccessor) {

    inline fun <reified T : Steps> steps(): ReadOnlyPropertyDelegate<T> {
        try {
            accessor.testContext.steps.setUpAsRequired(T::class as KClass<Steps>)
            return ReadOnlyPropertyDelegate { accessor.testContext.steps.get(T::class) }
        } catch (throwable: Throwable) {
            throw RuntimeException("Call on \"steps<${T::class.simpleName}>\" failed",
                throwable)
        }
    }

    inline fun <reified T : Any> dependency(): DependencyPropertyDelegate<T> {
        try {
            if (T::class.isSubclassOf(BaseSteps::class)) {
                throw RuntimeException("Steps classes can's be accessed as dependency, please " +
                    "use the correct function to access steps classes!")
            }
            val dependency = TestEnvironment.dependencies.configurations.getAssignableTo(T::class)
            val dependencyState = TestEnvironment.dependencies.states[dependency]
            return DependencyPropertyDelegate(dependencyState)
        } catch (throwable: Throwable) {
            throw RuntimeException("Call on \"dependency<${T::class.simpleName}>\" failed",
                throwable)
        }
    }

    inline fun <reified T : Any> factory() = try {
        ReadOnlyPropertyDelegate {
            accessor.testContext.factories.get<T>().run(accessor.testContext.steps.provider)
        }
    } catch (throwable: Throwable) {
        throw RuntimeException("Call on \"factory<${T::class.simpleName}>\" failed",
            throwable)
    }

    class ReadOnlyPropertyDelegate<out T>(private val getter: () -> T) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
    }

    class DependencyPropertyDelegate<out T : Any>(private val dependencyState: DependencyState<T>) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = dependencyState.instance
    }
}