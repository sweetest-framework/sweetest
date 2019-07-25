package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.accessor.StepsAccessor
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext

class StepsBuilder @Deprecated("Module testing configuration will be phased out") internal constructor(
    instance: BaseSteps, testContext: TestContext, moduleTestingConfiguration: ModuleTestingConfiguration?
) : BaseBuilder<StepsBuilder, StepsAccessor>(testContext, moduleTestingConfiguration) {

    constructor(instance: BaseSteps, testContext: TestContext) : this(instance, testContext, null)

    override fun buildInternal(): StepsAccessor {
        return StepsAccessor(testContext)
    }

    init {
        testContext.steps.setUpInstance(instance)
    }
}
