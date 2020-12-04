package dev.sweetest.v1

import dev.sweetest.internal.Steps
import dev.sweetest.internal.dependency.DependenciesTestContext
import dev.sweetest.internal.dependency.getDependencyDelegate
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.getStepsDelegate
import dev.sweetest.v1.internal.CommonBase
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

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
