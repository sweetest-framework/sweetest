package com.mysugr.sweetest.usecases

/**
 * Use cases for workflow.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep

fun subscribeWorkflow(
    workflowTestContext: WorkflowTestContext,
    workflowStep: WorkflowStep,
    handler: () -> Unit
) {
    workflowTestContext.subscribe(workflowStep, handler)
}

fun proceedWorkflow(
    workflowTestContext: WorkflowTestContext,
    toWorkflowStep: WorkflowStep = WorkflowStep.RUNNING
) {
    workflowTestContext.proceedTo(toWorkflowStep)
}

fun finishWorkflow(workflowTestContext: WorkflowTestContext) {
    workflowTestContext.finish()
}

fun hasWorkflowAlreadyStarted(workflowTestContext: WorkflowTestContext) =
    workflowTestContext.currentStep > WorkflowStep.INITIALIZE_STEPS
