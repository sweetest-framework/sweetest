package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment

open class DependenciesConfigurator {
    init {
        // Force initialization before everything else
        TestEnvironment
    }
}
