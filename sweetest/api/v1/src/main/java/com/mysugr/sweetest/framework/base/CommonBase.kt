package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.usecases.getDependencyDelegate
import com.mysugr.sweetest.usecases.getStepsDelegate
import com.mysugr.sweetest.framework.context.TestContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

abstract class CommonBase(internal val testContext: TestContext) : com.mysugr.sweetest.internal.TestElement

// --- region: Public API (the following inline functions should just be wrappers over implementation functions!)

inline fun <reified T : Any> CommonBase.dependency(): ReadOnlyProperty<CommonBase, T> =
    dependencyInternal(this, T::class)

inline fun <reified T : BaseSteps> CommonBase.steps(): ReadOnlyProperty<CommonBase, T> =
    stepsInternal(this, T::class)

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependencyInternal(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getDependencyDelegate(scope.testContext.dependencies, type)

@PublishedApi
internal fun <T : BaseSteps> stepsInternal(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getStepsDelegate(scope.testContext.steps, type)
