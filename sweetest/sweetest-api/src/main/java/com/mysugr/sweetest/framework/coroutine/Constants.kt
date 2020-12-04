package com.mysugr.sweetest.framework.coroutine

private const val PHASE_OUT_DEPRECATION_MESSAGE =
    "Will be phased out after API v1."

internal const val TEST_UTILS_DEPRECATION_MESSAGE =
    "$PHASE_OUT_DEPRECATION_MESSAGE Use official test utilities for kotlinx.coroutines."

internal const val EAGER_EXECUTION_DEPRECATION_MESSAGE =
    "$PHASE_OUT_DEPRECATION_MESSAGE Official test utilities for kotlinx.coroutines offer a proper approach for " +
        "ensuring correct execution order (\"eager execution\")."
