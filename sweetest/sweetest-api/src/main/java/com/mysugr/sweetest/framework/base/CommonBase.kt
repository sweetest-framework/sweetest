package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.usecases.getDependencyDelegate
import com.mysugr.sweetest.usecases.getSteps
import com.mysugr.sweetest.framework.context.TestContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

abstract class CommonBase(internal val testContext: TestContext) : com.mysugr.sweetest.internal.CommonBase

// --- region: Public API (inline functions should just be a wrapper over implementation functions!)

inline fun <reified T : Any> CommonBase.dependency(): ReadOnlyProperty<CommonBase, T> = dependency(this, T::class)
inline fun <reified T : BaseSteps> CommonBase.steps(): ReadOnlyProperty<CommonBase, T> = steps(this, T::class)

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependency(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getDependencyDelegate(scope.testContext.dependencies, type)

@PublishedApi
internal fun <T : BaseSteps> steps(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getSteps(scope.testContext.steps, type)
