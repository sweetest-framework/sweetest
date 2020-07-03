package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration

class ConfigurationsTestContext(private val factories: FactoriesTestContext) {

    private val moduleConfigurations = mutableListOf<ModuleTestingConfiguration>()

    val all = moduleConfigurations as List<ModuleTestingConfiguration>

    fun put(configuration: ModuleTestingConfiguration) {
        if (!moduleConfigurations.contains(configuration)) {
            moduleConfigurations.add(configuration)
            configuration.factories.forEach(factories::configure)
        }
    }
}
