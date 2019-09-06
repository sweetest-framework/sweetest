package com.mysugr.sweetest.framework.dependency2

import com.mysugr.sweetest.framework.base2.internal.dependency.DependencyInitializerReceiver
import kotlin.reflect.KClass

class DependencyRetriever(
        private val dependencyStateSource: DependencyStateProvider
) : DependenciesProvider {

    private val dependencyInitializationContext = object : DependencyInitializationContext {
        override val initializerReceiver = DependencyInitializerReceiver<Any>(this@DependencyRetriever)
    }

    override fun <T : Any> getInstanceOf(type: KClass<T>): T {
        // TODO accesses probably not yet assigned dependency state
        val state = dependencyStateSource.getDependencyStateFor(type)
        return getOrInitializeInstance(state, type)
    }

    private fun <T : Any> getOrInitializeInstance(dependencyState: DependencyState, type: KClass<T>): T {
        return if (dependencyState.isInitialized) {
            dependencyState.getInstanceChecked(type)
        } else {
            initializeAndGetInstance(dependencyState, type)
        }
    }

    private fun <T : Any> initializeAndGetInstance(
            dependencyState: DependencyState,
            type: KClass<T>): T {
        initializeInstance(dependencyState)
        return dependencyState.getInstanceChecked(type)
    }

    private fun initializeInstance(dependencyState: DependencyState): Any {
        val instance = dependencyState.initializer(dependencyInitializationContext.initializerReceiver)
                ?: throw IllegalStateException("Initializer is expected to return a non-null value")
        dependencyState.checkCorrectTypeOf(instance)
        return instance.also {
            dependencyState.instance = it
        }
    }
}