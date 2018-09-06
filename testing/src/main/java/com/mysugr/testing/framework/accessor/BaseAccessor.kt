package com.mysugr.testing.framework.accessor

import com.mysugr.testing.framework.context.TestContext

open class BaseAccessor internal constructor(@PublishedApi internal val testContext: TestContext) {

    val dependencies = DependenciesAccessor(this)
    fun dependencies(run: DependenciesAccessor.() -> Unit) = run(dependencies)

    val delegates = DelegatesAccessor(this)

    val states = StatesAccessor(this)
}
