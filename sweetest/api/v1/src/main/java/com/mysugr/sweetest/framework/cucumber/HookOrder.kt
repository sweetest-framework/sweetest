package com.mysugr.sweetest.framework.cucumber

object HookOrder {

    const val INITIALIZE_FRAMEWORK = 0
    const val INITIALIZE_STEPS = 10_000
    const val INITIALIZE_DEPENDENCIES = 20_000
    const val BEFORE_SET_UP = 29_000
    const val SET_UP = 30_000
    const val AFTER_SET_UP = 31_000

    const val TEAR_DOWN = 2_000
    const val AFTER_TEAR_DOWN = 1_000
    const val DONE = 0
}
