package com.mysugr.sweetest.framework.accessor

import com.mysugr.sweetest.framework.context.TestContext

open class BaseAccessor internal constructor(@PublishedApi internal val testContext: TestContext) {

    val dependencies = DependenciesAccessor(this)
    fun dependencies(run: DependenciesAccessor.() -> Unit) = run(dependencies)

    val delegates = DelegatesAccessor(this)

    val states = StatesAccessor(this)
}
