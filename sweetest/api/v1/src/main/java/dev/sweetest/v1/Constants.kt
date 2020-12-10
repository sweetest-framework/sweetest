package dev.sweetest.v1

internal const val OUT_OF_SCOPE_DEPRECATION_MESSAGE =
    "Phased out after v1 as out-of-scope."

internal const val MODULE_CONFIG_DEPRECATION_MESSAGE =
    "Strong recommendation to remove module configuration"

internal const val BASE_CLASS_DEPRECATION_MESSAGE =
    "This is a legacy v1 compatibility type – only use for old test code! " +
        "For new tests always use types from the `dev.sweetest.v2` package! " +
        "If there are no tests referencing the old `dev.sweetest.v1` package anymore, " +
        "remove the `sweetest-compat-v1` dependency and from then on only " +
        "use one of the `sweetest-*` dependencies (e.g. `sweetest-junit4`)!"

private const val COROUTINES_PHASE_OUT_DEPRECATION_MESSAGE =
    "Will be phased out after API v1."

internal const val COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE =
    "$COROUTINES_PHASE_OUT_DEPRECATION_MESSAGE Use official test utilities for kotlinx.coroutines."

internal const val COROUTINES_EAGER_EXECUTION_DEPRECATION_MESSAGE =
    "$COROUTINES_PHASE_OUT_DEPRECATION_MESSAGE Official test utilities for kotlinx.coroutines offer a proper approach for " +
        "ensuring correct execution order (\"eager execution\")."
