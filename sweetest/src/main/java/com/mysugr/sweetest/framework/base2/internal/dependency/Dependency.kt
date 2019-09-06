package com.mysugr.sweetest.framework.base2.internal.dependency

import com.mysugr.sweetest.framework.context2.TestContext
import kotlin.reflect.KClass

fun <T : Any> consumeDependencyViaDelegate(testContext: TestContext, dependencyClass: KClass<T>): DependencyDelegate<T> {
    TODO()
}