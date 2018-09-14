package com.mysugr.sweetest.framework.cucumber

object HookOrder {

    const val INITIALIZE_FRAMEWORK = 0
    const val INITIALIZE_STEPS = 10000
    const val INITIALIZE_DEPENDENCIES = 20000
    const val SETUP = 30000
}