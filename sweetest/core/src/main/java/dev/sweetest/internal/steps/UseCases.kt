/**
 * Use cases for steps.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

package dev.sweetest.internal.steps

import dev.sweetest.internal.SweetestException
import dev.sweetest.internal.InternalBaseSteps
import dev.sweetest.internal.InternalBaseTestElement
import dev.sweetest.internal.InternalSweetestApi
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@InternalSweetestApi
fun registerStepsInstance(stepsTestContext: StepsTestContext, instance: InternalBaseSteps) {
    stepsTestContext.setUpInstance(instance)
}

@InternalSweetestApi
fun <T : InternalBaseSteps> notifyStepsRequired(stepsTestContext: StepsTestContext, stepsType: KClass<T>) {
    @Suppress("UNCHECKED_CAST")
    stepsTestContext.setUpAsRequired(stepsType as KClass<InternalBaseSteps>)
}

@InternalSweetestApi
fun <T : InternalBaseSteps> getStepsDelegate(
    stepsTestContext: StepsTestContext,
    stepsType: KClass<T>
): ReadOnlyProperty<InternalBaseTestElement, T> {
    try {
        notifyStepsRequired(stepsTestContext, stepsType = stepsType)
        return object : ReadOnlyProperty<InternalBaseTestElement, T> {
            override fun getValue(thisRef: InternalBaseTestElement, property: KProperty<*>): T {
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
