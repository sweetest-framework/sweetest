package com.mysugr.sweetest.framework.coroutine

interface CoroutinesTestConfigurator {

    fun useLegacyCoroutineScope()

    /**
     * This configuration can be set multiple times but not changed. This prevents conflicting expectations in the test
     * system
     */
    fun autoSetMainCoroutineDispatcher(value: Boolean)

    /**
     * This configuration can be set multiple times but not changed. This prevents conflicting expectations in the test
     * system
     */
    fun autoCancelTestCoroutines(value: Boolean)
}

internal class CoroutinesTestConfiguration : CoroutinesTestConfigurator {

    var data = CoroutinesTestConfigurationData()
        private set

    override fun useLegacyCoroutineScope() {
        data = data.copy(
            useLegacyTestCoroutine = true
        )
    }

    /**
     * This configuration can be set multiple times but not changed. This prevents conflicting expectations in the test
     * system
     */
    override fun autoSetMainCoroutineDispatcher(value: Boolean) {
        val previousValue = data.autoSetMainCoroutineDispatcher
        require(previousValue == null || previousValue == value) {
            getCantChangeErrorMessage(
                "autoSetMainCoroutineDispatcher",
                previousValue!!
            )
        }
        data = data.copy(
            autoSetMainCoroutineDispatcher = value
        )
    }

    /**
     * This configuration can be set multiple times but not changed. This prevents conflicting expectations in the test
     * system
     */
    override fun autoCancelTestCoroutines(value: Boolean) {
        val previousValue = data.autoCancelTestCoroutines
        require(previousValue == null || previousValue == value) {
            getCantChangeErrorMessage("autoCancelTestCoroutines", previousValue!!)
        }
        data = data.copy(
            autoCancelTestCoroutines = value
        )
    }

    private fun getCantChangeErrorMessage(property: String, previousValue: Boolean): String {
        return "$property was set to $previousValue before somewhere in your test system, " +
            "it can't be changed anymore. This error is to ensure consistent expectations throughout " +
            "your test system."
    }
}