package com.mysugr.testing.framework.base

import com.mysugr.testing.framework.configuration.ModuleTestingConfiguration

abstract class ModuleCucumberHooks constructor(
    val moduleTestingConfigurations: ModuleTestingConfiguration
) {

    /**
     * Has to be overridden by implementer with annotation @Before(order = HookOrder.INITIALIZE_FRAMEWORK) so it's
     * automatically initialized by Cucumber
     */
    abstract fun initialize()
}
