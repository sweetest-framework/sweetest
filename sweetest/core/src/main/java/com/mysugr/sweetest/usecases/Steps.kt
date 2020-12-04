/**
 * Use cases for steps.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

package com.mysugr.sweetest.usecases

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.internal.TestElement
import com.mysugr.sweetest.internal.Steps
import dev.sweetest.internal.InternalSweetestApi
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@InternalSweetestApi
fun registerStepsInstance(stepsTestContext: StepsTestContext, instance: Steps) {
    stepsTestContext.setUpInstance(instance)
}

@InternalSweetestApi
fun <T : Steps> notifyStepsRequired(stepsTestContext: StepsTestContext, stepsType: KClass<T>) {
    @Suppress("UNCHECKED_CAST")
    stepsTestContext.setUpAsRequired(stepsType as KClass<Steps>)
}

@InternalSweetestApi
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
