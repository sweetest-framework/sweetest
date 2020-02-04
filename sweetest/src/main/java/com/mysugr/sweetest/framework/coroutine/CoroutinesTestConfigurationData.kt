package com.mysugr.sweetest.framework.coroutine

data class CoroutinesTestConfigurationData(
    /**
     * Use [LegacyCoroutinesTestContext] instead of [DefaultCoroutinesTestContext]
     */
    val useLegacyTestCoroutine: Boolean = false,

    /**
     * Default behavior is that [kotlinx.coroutines.test.setMain] is called with a CoroutineDispatcher from the test's
     * CoroutineScope for each test. That behavior can be disabled by setting this to false.
     */
    val autoSetMainCoroutineDispatcher: Boolean? = null,

    /**
     * The [kotlinx.coroutines.Job] that runs the test can still be active in case children jobs exist that are still
     * running. This option can be enabled to automatically close the parent job including all its children. By default
     * this is not enabled.
     */
    val autoCancelTestCoroutines: Boolean? = null
) {
    object Defaults {
        val autoSetMainCoroutineDispatcher = true
        val autoCancelTestCoroutines = false
    }
}

val CoroutinesTestConfigurationData.autoCancelTestCoroutinesEnabled
    get() = this.autoCancelTestCoroutines
        ?: CoroutinesTestConfigurationData.Defaults.autoCancelTestCoroutines

val CoroutinesTestConfigurationData.autoSetMainCoroutineDispatcherEnabled
    get() = this.autoSetMainCoroutineDispatcher
        ?: CoroutinesTestConfigurationData.Defaults.autoSetMainCoroutineDispatcher