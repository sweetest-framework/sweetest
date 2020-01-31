package com.mysugr.sweetest.framework.coroutine

data class CoroutinesTestConfigurationData(
    /**
     * Use [LegacyCoroutinesTestContext] instead of [DefaultCoroutinesTestContext]
     */
    val useLegacyTestCoroutine: Boolean = false,

    /**
     * Default behavior is that [kotlinx.coroutines.test.setMain] is called with a CoroutineDispatcher from the test's
     * CoroutineScope for each test. This can be disabled by setting this to false.
     */
    val autoSetMainCoroutineDispatcher: Boolean? = null
)