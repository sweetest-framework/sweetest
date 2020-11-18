package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.api.getDependencyDelegate
import com.mysugr.sweetest.api.getStepsFinal
import com.mysugr.sweetest.framework.context.TestContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

abstract class CommonBase(@PublishedApi internal val testContext: TestContext) : com.mysugr.sweetest.internal.CommonBase

// --- region: Published API (necessary to keep inlined footprint as small as possible)

inline fun <reified T : Any> CommonBase.dependency(): ReadOnlyProperty<CommonBase, T> = dependency(this, T::class)
inline fun <reified T : BaseSteps> CommonBase.steps(): ReadOnlyProperty<CommonBase, T> = steps(this, T::class)

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependency(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getDependencyDelegate(type)

@PublishedApi
internal fun <T : BaseSteps> steps(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getStepsFinal(scope.testContext.steps, type)
