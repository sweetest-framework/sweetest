package com.mysugr.sweetest.v2.framework.core.workflow

interface WorkflowSubscribeable {
    fun on(workflowStep: WorkflowStep, block: () -> Unit)
}