package com.mysugr.sweetest.framework.context2

import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.context2.internal.DependenciesTestContext

class TestContext {
    @PublishedApi
    internal val dependencies = DependenciesTestContext()

    @PublishedApi
    internal val steps = StepsTestContext(this)

    @PublishedApi
    internal val workflow = WorkflowTestContext(steps)
}