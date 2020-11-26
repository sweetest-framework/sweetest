package com.mysugr.sweetest.usecases

import com.mysugr.sweetest.framework.environment.TestEnvironment

/**
 * Use cases for the global sweetest environment.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

// Temporary means of initializing the environment as long as TestEnvironment is still here
fun ensureEnvironmentInitialized() {
    TestEnvironment
}

// Temporary means of resetting the state of the environment as long as TestEnvironment is still here
fun resetEnvironment() {
    TestEnvironment.reset()
}

/**
 * Resets global, test-independent caches (dependency configuration from module testing configuration)
 */
fun resetEnvironmentFully() {
    TestEnvironment.fullReset()
}
