package dev.sweetest.v2.cucumber2

object HookOrder {

    const val INITIALIZE_FRAMEWORK = 0
    const val INITIALIZE_STEPS = 10_000 // Needs to happen together with all @Before function with default order = 10_000
    const val INITIALIZE_DEPENDENCIES = 10_100
    const val BEFORE_SET_UP = 10_200
    const val SET_UP = 10_300
    const val AFTER_SET_UP = 10_400
    // Test function is executed at that point
    // Other @After routines with default order 10_000 are executed at that point
    const val TEAR_DOWN = 200
    const val AFTER_TEAR_DOWN = 100
    const val DONE = 0 // Needs to be the last thing happening
}
