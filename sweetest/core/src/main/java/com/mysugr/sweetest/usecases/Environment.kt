/**
 * Use cases for the global sweetest environment.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

package com.mysugr.sweetest.usecases

import com.mysugr.sweetest.BDD_INCLUSION_MESSAGE
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.environment.TestEnvironment
import dev.sweetest.internal.InternalSweetestApi
import dev.sweetest.internal.TestContext

private const val ENVIRONMENT_STATE_ERROR_MESSAGE =
    "Make sure you aren't running tests in parallel " +
        "within the same process! If you are using Cucumber, $BDD_INCLUSION_MESSAGE."

private var currentTestContext: TestContext? = null

@InternalSweetestApi
fun ensureEnvironmentInitialized() {
    TestEnvironment
}

@InternalSweetestApi
fun startEnvironment(): TestContext {
    if (currentTestContext != null) throw SweetestException(
        "Can't start test environment: It has already been started! $ENVIRONMENT_STATE_ERROR_MESSAGE"
    )
    return TestContext().also {
        currentTestContext = it
    }
}

@InternalSweetestApi
fun getCurrentTestContext(): TestContext {
    return currentTestContext ?: throw SweetestException(
        "Can't retrieve state from test environment: It has not yet been started! $ENVIRONMENT_STATE_ERROR_MESSAGE"
    )
}

@InternalSweetestApi
fun resetEnvironment() {
    if (currentTestContext == null) {
        throw SweetestException(
            "Can't stop test environment: It has not previously been started! $ENVIRONMENT_STATE_ERROR_MESSAGE"
        )
    }
    ensureEnvironmentReset()
}

/**
 * Same as [resetEnvironment] but without checking the previous state
 */
@InternalSweetestApi
fun ensureEnvironmentReset() {
    TestEnvironment.reset()
    currentTestContext = null
}

/**
 * Same as [ensureEnvironmentReset] but also resets global, test-independent caches
 * (dependency configuration from module testing configuration).
 */
@InternalSweetestApi
fun resetEnvironmentFully() {
    TestEnvironment.fullReset()
    ensureEnvironmentReset()
}
