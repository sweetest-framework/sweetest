package com.mysugr.sweetest.framework.coroutine

internal class CoroutinesTestConfiguration {
    var data = CoroutinesTestConfigurationData()

    fun useLegacyCoroutineScope() {
        data = data.copy(
            useLegacyTestCoroutine = true
        )
    }

    /**
     * This configuration can be set multiple times but not changed. This prevents conflicting expectations in the test
     * system
     */
    fun autoSetMainCoroutineDispatcher(value: Boolean) {
        val previousValue = data.autoSetMainCoroutineDispatcher
        if (previousValue != null && previousValue != value) {
            error(getCantChangeErrorMessage("autoSetMainCoroutineDispatcher", previousValue))
        } else {
            data = data.copy(
                autoSetMainCoroutineDispatcher = value
            )
        }
    }

    /**
     * This configuration can be set multiple times but not changed. This prevents conflicting expectations in the test
     * system
     */
    fun autoCleanupTestCoroutines(value: Boolean) {
        val previousValue = data.autoCleanupTestCoroutines
        if (previousValue != null && previousValue != value) {
            error(getCantChangeErrorMessage("autoCleanupTestCoroutines", previousValue))
        } else {
            data = data.copy(
                autoCleanupTestCoroutines = value
            )
        }
    }

    private fun getCantChangeErrorMessage(property: String, previousValue: Boolean): String {
        return "$property was set to $previousValue before somewhere in your test system, " +
            "it can't be enabled anymore. This error is to ensure consistent expectations throughout " +
            "your test system."
    }
}