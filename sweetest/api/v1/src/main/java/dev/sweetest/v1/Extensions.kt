package dev.sweetest.v1

import dev.sweetest.internal.InternalBaseSteps
import dev.sweetest.internal.dependency.DependenciesTestContext
import dev.sweetest.internal.dependency.getDependencyDelegate
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.getStepsDelegate
import dev.sweetest.v1.internal.ApiTestElement
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

// --- region: Public API (the following inline functions should just be wrappers over implementation functions!)

/**
 * Lets you consume an instance from sweetest's dependency management. These dependencies are managed as singletons,
 * which means that all calls to [dependency] across steps and test classes all return the same instance.
 */
inline fun <reified T : Any> ApiTestElement.dependency(): ReadOnlyProperty<ApiTestElement, T> =
    dependencyInternal(this, T::class)

/**
 * Lets you consume an instance of a sweetest steps class. These are managed as singletons, which means that all
 * calls to [dependency] across steps and test classes all return the same instance.
 */
inline fun <reified T : InternalBaseSteps> ApiTestElement.steps(): ReadOnlyProperty<ApiTestElement, T> =
    stepsInternal(this, T::class)

// --- region: Internal API

@PublishedApi
internal fun <T : Any> dependencyInternal(scope: ApiTestElement, type: KClass<T>): ReadOnlyProperty<ApiTestElement, T> =
    getDependencyDelegate(scope.testContext[DependenciesTestContext], type)

@PublishedApi
internal fun <T : InternalBaseSteps> stepsInternal(scope: ApiTestElement, type: KClass<T>): ReadOnlyProperty<ApiTestElement, T> =
    getStepsDelegate(scope.testContext[StepsTestContext], type)
