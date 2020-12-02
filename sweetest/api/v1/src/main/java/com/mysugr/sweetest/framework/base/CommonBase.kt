package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.getDependencyDelegate
import com.mysugr.sweetest.usecases.getStepsDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

abstract class CommonBase(internal val testContext: TestContext) : com.mysugr.sweetest.internal.TestElement

// --- region: Public API (the following inline functions should just be wrappers over implementation functions!)

inline fun <reified T : Any> CommonBase.dependency(): ReadOnlyProperty<CommonBase, T> =
    dependencyInternal(this, T::class)

inline fun <reified T : Steps> CommonBase.steps(): ReadOnlyProperty<CommonBase, T> =
    stepsInternal(this, T::class)

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependencyInternal(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getDependencyDelegate(scope.testContext[DependenciesTestContext], type)

@PublishedApi
internal fun <T : Steps> stepsInternal(scope: CommonBase, type: KClass<T>): ReadOnlyProperty<CommonBase, T> =
    getStepsDelegate(scope.testContext[StepsTestContext], type)
