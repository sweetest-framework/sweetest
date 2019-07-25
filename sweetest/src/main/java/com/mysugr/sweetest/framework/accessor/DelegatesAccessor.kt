package com.mysugr.sweetest.framework.accessor

import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.Steps
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

class DelegatesAccessor(@PublishedApi internal val accessor: BaseAccessor) {

    inline fun <reified T : Steps> steps(): ReadOnlyPropertyDelegate<T> {
        try {
            accessor.testContext.steps.setUpAsRequired(T::class as KClass<Steps>)
            return ReadOnlyPropertyDelegate { accessor.testContext.steps.get(T::class) }
        } catch (throwable: Throwable) {
            throw RuntimeException(
                "Call on \"steps<${T::class.simpleName}>\" failed",
                throwable
            )
        }
    }

    inline fun <reified T : Any> dependency(): DependencyPropertyDelegate<T> {
        try {
            if (T::class.isSubclassOf(BaseSteps::class)) {
                throw RuntimeException(
                    "Steps classes can's be accessed as dependency, please " +
                        "use the correct function to access steps classes!"
                )
            }
            val dependencyState = TestEnvironment.dependencies.states[T::class]
            if (accessor.testContext.testContextConfiguration.hasModuleTestingConfiguration) {
                if (!dependencyState.hasConfiguration) {
                    throw RuntimeException(
                        "ModuleTestingConfiguration entry for dependency \"${T::class.simpleName}\" missing"
                    )
                }
            }
            return DependencyPropertyDelegate(dependencyState)
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

    class DependencyPropertyDelegate<out T : Any>(private val dependencyState: DependencyState<T>) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = dependencyState.instance
    }
}
