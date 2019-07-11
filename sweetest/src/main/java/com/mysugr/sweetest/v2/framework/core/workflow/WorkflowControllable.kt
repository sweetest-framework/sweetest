package com.mysugr.sweetest.v2.framework.core.workflow

interface WorkflowControllable {
    fun proceedTo(workflowStep: WorkflowStep)
}