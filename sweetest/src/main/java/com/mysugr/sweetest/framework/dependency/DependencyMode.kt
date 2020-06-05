package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.build.BaseBuilder

enum class DependencyMode {
    REAL,
    MOCK,
    SPY,

    /**
     * Dependency that is provided with [BaseBuilder.provide], without specifying a special mode.
     * This mode is distinct from the other modes like [REAL], [MOCK] because [PROVIDED] instances
     * are not classified as either [REAL], [MOCK] or [SPY].
     */
    PROVIDED
}
