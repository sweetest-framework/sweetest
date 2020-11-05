package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep

interface WorkflowController {
    fun proceedTo(step: InitializationStep)
    fun run()
    fun finish()
}
