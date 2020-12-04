/**
 * Use cases for workflow.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

package com.mysugr.sweetest.usecases

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep
import dev.sweetest.internal.InternalSweetestApi

@InternalSweetestApi
fun subscribeWorkflow(
    workflowTestContext: WorkflowTestContext,
    workflowStep: WorkflowStep,
    handler: () -> Unit
) {
    workflowTestContext.subscribe(workflowStep, handler)
}

@InternalSweetestApi
fun proceedWorkflow(
    workflowTestContext: WorkflowTestContext,
    toWorkflowStep: WorkflowStep = WorkflowStep.RUNNING
) {
    workflowTestContext.proceedTo(toWorkflowStep)
}

@InternalSweetestApi
fun finishWorkflow(workflowTestContext: WorkflowTestContext) {
    workflowTestContext.finish()
}

@InternalSweetestApi
fun hasWorkflowAlreadyStarted(workflowTestContext: WorkflowTestContext) =
    workflowTestContext.currentStep > WorkflowStep.INITIALIZE_STEPS
