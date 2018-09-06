package com.mysugr.testing.framework.dependency

import com.mysugr.testing.framework.environment.TestEnvironment

open class DependenciesConfigurator {
    init {
        // Force initialization before everything else
        TestEnvironment
    }
}
