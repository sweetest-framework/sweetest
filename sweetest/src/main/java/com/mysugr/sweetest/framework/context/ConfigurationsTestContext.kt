package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration

class ConfigurationsTestContext(private val factories: FactoriesTestContext) {

    private val moduleConfigurations = mutableListOf<ModuleTestingConfiguration>()

    val all = moduleConfigurations as List<ModuleTestingConfiguration>

    fun put(configuration: ModuleTestingConfiguration?) {
        if (!moduleConfigurations.contains(configuration)) {
            configuration?.let {
                moduleConfigurations.add(it)
                it.factories.forEach(factories::configure)
            }
        }
    }
}
