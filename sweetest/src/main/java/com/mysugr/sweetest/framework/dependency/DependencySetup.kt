package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.DependencySetupHandler
import kotlin.reflect.KClass

object DependencySetup {

    private var _setupHandler: DependencySetupHandler? = null
    val setupHandler: DependencySetupHandler
        get() = _setupHandler ?: throw IllegalStateException("initDependencies() has not yet been called")

    fun init(setupHandler: DependencySetupHandler) {
        _setupHandler = setupHandler
    }

    @Deprecated("Preliminary solution")
    fun addConfiguration(configuration: DependencyConfiguration<*>) = setupHandler.addConfiguration(configuration)

    inline fun <reified T : Any> add(
        alias: KClass<*>? = null
    ): DependencyConfiguration<T> = setupHandler.addConfiguration(T::class, alias = alias)

    inline fun <reified T : Any> addReal(
        noinline realInitializer: DependencyInitializer<T>
    ): DependencyConfiguration<T> =
        setupHandler.addConfiguration(T::class, realInitializer)

    inline fun <reified T : Any> addRealRequired(
        alias: KClass<*>? = null,
        noinline realInitializer: DependencyInitializer<T>
    ): DependencyConfiguration<T> =
        setupHandler.addConfiguration(T::class, realInitializer, null, DependencyMode.REAL, alias)

    inline fun <reified T : Any> addMocked(
        alias: KClass<*>? = null,
        noinline mockInitializer: DependencyInitializer<T>
    ): DependencyConfiguration<T> =
        setupHandler.addConfiguration(T::class, null, mockInitializer, null, alias)

    inline fun <reified T : Any> addMockedRequired(
        alias: KClass<*>? = null,
        noinline mockInitializer: DependencyInitializer<T>
    ): DependencyConfiguration<T> =
        setupHandler.addConfiguration(T::class, null, mockInitializer, DependencyMode.MOCK, alias)
}
