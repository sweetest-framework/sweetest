package com.mysugr.sweetest.framework.flow

interface WorkflowProvider {
    fun subscribe(step: InitializationStep, handler: () -> Unit)
}
