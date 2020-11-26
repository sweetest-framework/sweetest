package com.mysugr.sweetest.usecases

/**
 * Use cases for steps.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.internal.CommonBase
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.util.PropertyDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

fun initializeSteps(stepsTestContext: StepsTestContext, instance: Steps) {
    stepsTestContext.setUpInstance(instance)
}

fun <T : Steps> notifyStepsRequired(stepsTestContext: StepsTestContext, type: KClass<T>) {
    stepsTestContext.setUpAsRequired(type as KClass<Steps>)
}

fun <T : Steps> getSteps(stepsTestContext: StepsTestContext, type: KClass<T>): ReadOnlyProperty<CommonBase, T> {
    try {
        notifyStepsRequired(stepsTestContext, type)
        return PropertyDelegate {
            try {
                stepsTestContext.get(type)
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
