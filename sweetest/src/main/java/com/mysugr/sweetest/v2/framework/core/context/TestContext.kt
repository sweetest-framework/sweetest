package com.mysugr.sweetest.v2.framework.core.context

import com.mysugr.sweetest.v2.framework.component.ApiComponent
import com.mysugr.sweetest.v2.framework.component.SingletonComponent

/**
 * Holds a singleton sweetest component, but on creation, ensures there is a cleaned-up state.
 * The component holds all internal dependencies of the sweetest framework.
 */
class TestContext {
    val component: ApiComponent = SingletonComponent.also {
        it.stateStore.reset()
    }
}