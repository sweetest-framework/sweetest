package com.mysugr.sweetest.usecases

/**
 * Use cases for steps.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.internal.TestElement
import com.mysugr.sweetest.internal.Steps
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun registerStepsInstance(stepsTestContext: StepsTestContext, instance: Steps) {
    stepsTestContext.setUpInstance(instance)
}

fun <T : Steps> notifyStepsRequired(stepsTestContext: StepsTestContext, stepsType: KClass<T>) {
    stepsTestContext.setUpAsRequired(stepsType as KClass<Steps>)
}

fun <T : Steps> getStepsDelegate(
    stepsTestContext: StepsTestContext,
    stepsType: KClass<T>
): ReadOnlyProperty<TestElement, T> {
    try {
        notifyStepsRequired(stepsTestContext, stepsType = stepsType)
        return object : ReadOnlyProperty<TestElement, T> {
            override fun getValue(thisRef: TestElement, property: KProperty<*>): T {
                return try {
                    stepsTestContext.get(stepsType)
                } catch (throwable: Throwable) {
                    throw SweetestException(
                        "Providing steps class instance for \"steps<${stepsType.simpleName}>\" failed",
                        throwable
                    )
                }
            }
        }
    } catch (throwable: Throwable) {
        throw RuntimeException(
            "Call on \"steps<$stepsType>\" failed",
            throwable
        )
    }
}
