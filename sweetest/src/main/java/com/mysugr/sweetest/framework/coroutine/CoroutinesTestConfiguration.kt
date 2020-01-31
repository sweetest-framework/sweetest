package com.mysugr.sweetest.framework.coroutine

internal class CoroutinesTestConfiguration {
    var data = CoroutinesTestConfigurationData()

    fun useLegacyCoroutineScope() {
        data = data.copy(
            useLegacyTestCoroutine = true
        )
    }

    fun autoSetMainCoroutineDispatcher(value: Boolean) {
        val previousValue = data.autoSetMainCoroutineDispatcher
        if (previousValue != null && previousValue != value) {
            error(
                "autoSetMainCoroutineDispatcher was set to $previousValue before somewhere in your test system, " +
                    "it can't be enabled anymore. This error is to ensure consistent expectations throughout " +
                    "your test system."
            )
        } else {
            data = data.copy(
                autoSetMainCoroutineDispatcher = value
            )
        }
    }
}