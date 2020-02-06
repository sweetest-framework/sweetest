package com.mysugr.sweetest.framework.coroutine

/**
 * The configurator makes sure that there can not be any conflicting configurations.
 *
 * **Example 1:**
 * - Test calls [autoSetMainCoroutineDispatcher] with argument `false`
 * - One of its steps classes calls [autoSetMainCoroutineDispatcher] with argument `true` --> causes exception
 *
 * **Example 2:**
 * - Test calls [autoSetMainCoroutineDispatcher] with argument `false`
 * - One of its steps classes calls [autoSetMainCoroutineDispatcher] with argument `false` --> OK
 * - Result: [autoSetMainCoroutineDispatcher] is ultimately `false`
 *
 * However, [useLegacyCoroutineScope] has a slightly different behavior, as its state has to be known at the time of
 * test initialization to allow early access to a [kotlinx.coroutines.CoroutineScope]. Therefore if you want a test to
 * have a legacy coroutines behavior, you _have to_ specify that on the test level.
 *
 * **Example 3:**
 * - Test doesn't call `useLegacyCoroutineScope`
 * - Result: `false` (use default coroutine behavior)
 * - A steps class calls `useLegacyCoroutineScope(true)` --> exception
 */
interface CoroutinesTestConfigurator {

    fun useLegacyCoroutineScopeOnTestLevel(value: Boolean)

    fun useLegacyCoroutineScopeOnStepsLevel(value: Boolean)

    fun autoSetMainCoroutineDispatcher(value: Boolean)

    fun autoCancelTestCoroutines(value: Boolean)
}

internal class CoroutinesTestConfiguration : CoroutinesTestConfigurator {

    var data = CoroutinesTestConfigurationData()
        private set

    override fun useLegacyCoroutineScopeOnTestLevel(value: Boolean) {
        val previousValue = data.useLegacyTestCoroutine
        require(previousValue == null || previousValue == value) {
            getCantChangeErrorMessage(
                "useLegacyTestCoroutine",
                previousValue!!
            )
        }
        data = data.copy(
            useLegacyTestCoroutine = value
        )
    }

    override fun useLegacyCoroutineScopeOnStepsLevel(value: Boolean) {
        val previousValue = data.useLegacyTestCoroutine
        require(previousValue == null || previousValue == value) {
            getCanOnlyBeSetOnTestLevelErrorMessage(
                "useLegacyTestCoroutine",
                previousValue!!
            )
        }
    }

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

    private fun getCanOnlyBeSetOnTestLevelErrorMessage(property: String, previousValue: Boolean): String {
        return "$property was set to $previousValue before at the test level, it can't be changed anymore. " +
            "This specific property is not possible to be changed on a steps level, too."
    }
}