package com.mysugr.sweetest.api

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.internal.CommonBase
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.util.PropertyDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

fun <T : Steps> getStepsFinal(stepsTestContext: StepsTestContext, type: KClass<T>): ReadOnlyProperty<CommonBase, T> {
    try {
        stepsTestContext.setUpAsRequired(type as KClass<Steps>)
        return PropertyDelegate {
            try {
                stepsTestContext.get(type) as T
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
