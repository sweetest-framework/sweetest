package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration

abstract class ModuleBddHooks constructor(
    val moduleTestingConfigurations: ModuleTestingConfiguration
) {

    /**
     * Has to be overridden by implementer with annotation @Before(order =
     * HookOrder.INITIALIZE_FRAMEWORK) so it's automatically initialized by BDD framework
     */
    abstract fun initialize()
}
