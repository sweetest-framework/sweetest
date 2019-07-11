package com.mysugr.sweetest.v2.framework.core.actor

import kotlin.reflect.KClass

interface ActorsAccessor {
    fun <T : Any> registerAsMock(actor: IActor, requestedType: KClass<T>, initializeMock: (mock: T) -> Unit)
    fun <T : Any> registerAsAuto(actor: IActor, requestedType: KClass<T>, setUpInstance: (instance: T) -> Unit)
    fun <T : Any> registerAsCustom(actor: IActor, requestedType: KClass<T>, onCreateInstance: () -> T)
    fun <T : Any> registerAsMockDependency(actor: IActor, requestedType: KClass<T>, initializeMock: (mock: T) -> Unit)
    fun <T : Any> registerAsAutoDependency(
        actor: IActor, requestedType: KClass<T>,
        setUpInstance: (instance: T) -> Unit
    )
    fun <T : Any> registerAsCustomDependency(actor: IActor, requestedType: KClass<T>, onCreateInstance: () -> T)
    fun setDependencyNeeded(actor: IActor, requestedType: KClass<*>)
    fun registerDependencyAlias(actor: IActor, dependencyType: KClass<*>, aliasType: KClass<*>)
    fun <T : Any> getActedOnInstance(actor: IActor, actedOnType: KClass<T>): T
    fun <T : Any> getDependencyInstance(actor: IActor, dependencyType: KClass<T>): T
}