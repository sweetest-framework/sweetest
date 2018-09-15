package com.mysugr.android.testing.example.app

import com.mysugr.sweetest.framework.base.ModuleBddHooks
import com.mysugr.sweetest.framework.cucumber.HookOrder
import cucumber.api.java.Before

class AppCucumberHooks : ModuleBddHooks(appModuleTestingConfiguration) {

    @Before(order = HookOrder.INITIALIZE_FRAMEWORK)
    override fun initialize() { }

}
