package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.DependencySetupHandler

object DependencySetup {

    private var _setupHandler: DependencySetupHandler? = null
    val setupHandler: DependencySetupHandler
        get() = _setupHandler ?: error("Not initialized")

    fun init(setupHandler: DependencySetupHandler) {
        _setupHandler = setupHandler
    }

    fun addConfiguration(configuration: DependencyConfiguration<*>) = setupHandler.addConfiguration(configuration)
}
