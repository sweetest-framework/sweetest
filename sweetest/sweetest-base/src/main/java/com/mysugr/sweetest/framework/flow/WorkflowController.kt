package com.mysugr.sweetest.framework.flow

interface WorkflowController {
    fun proceedTo(step: InitializationStep)
    fun run()
    fun finish()
}
