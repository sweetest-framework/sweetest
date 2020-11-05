package com.mysugr.sweetest.framework.dependency

enum class DependencyMode {

    // Legacy:

    REAL,
    MOCK,
    SPY,

    // New:

    // The distinction between REAL and AUTO_PROVIDED is made in order to distinguish between a dependency being
    // configured from new `provide` or old `requireReal` or `realOnly`

    /**
     * Dependency that is provided with a custom initializer with BaseBuilder.provide.
     *
     * This mode is distinct from the other modes like [REAL], [MOCK] because [PROVIDED] instances
     * are not classified as either [REAL], [MOCK] or [SPY].
     */
    PROVIDED,

    /**
     * Dependency that is configured with [BaseBuilder.provide] to be auto-provided.
     *
     * This mode is distinct from the other modes like [REAL], [MOCK] because [PROVIDED] instances
     * are not classified as either [REAL], [MOCK] or [SPY].
     */
    AUTO_PROVIDED
}
