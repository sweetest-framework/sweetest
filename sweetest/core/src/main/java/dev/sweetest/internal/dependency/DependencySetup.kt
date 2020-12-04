package dev.sweetest.internal.dependency

import dev.sweetest.internal.environment.DependencySetupHandler

object DependencySetup {

    private var _setupHandler: DependencySetupHandler? = null
    val setupHandler: DependencySetupHandler
        get() = _setupHandler ?: error("Not initialized")

    fun init(setupHandler: DependencySetupHandler) {
        _setupHandler = setupHandler
    }

    fun addConfiguration(configuration: DependencyConfiguration<*>) = setupHandler.addConfiguration(configuration)
}
