package com.mysugr.sweetest.framework.accessor

import com.mysugr.sweetest.framework.context.TestContext

open class BaseAccessor internal constructor(@PublishedApi internal val testContext: TestContext) {
    val delegates = DelegatesAccessor(this)
}
