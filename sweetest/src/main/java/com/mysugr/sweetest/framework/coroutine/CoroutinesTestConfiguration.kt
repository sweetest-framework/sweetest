package com.mysugr.sweetest.framework.coroutine

internal class CoroutinesTestConfiguration {
    var data = CoroutinesTestConfigurationData()

    fun useLegacyCoroutineScope() {
        data = data.copy(
            useLegacyTestCoroutine = true
        )
    }
}