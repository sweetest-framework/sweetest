package com.mysugr.sweetest

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
