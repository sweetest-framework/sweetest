package com.mysugr.sweetest.usecases

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.flow.InitializationStep

/**
 * Use cases for workflow.
 *
 * - achieves internal API stability for calls towards the core while the core can freely be refactored
 * - adds user-facing exceptions that are shared among different versions of public APIs
 */

fun subscribeWorkflow(
    workflowTestContext: WorkflowTestContext,
    initializationStep: InitializationStep,
    handler: () -> Unit
) {
    workflowTestContext.subscribe(initializationStep, handler)
}

fun proceedWorkflow(
    workflowTestContext: WorkflowTestContext,
    toInitializationStep: InitializationStep = InitializationStep.RUNNING
) {
    workflowTestContext.proceedTo(toInitializationStep)
}

fun finishWorkflow(workflowTestContext: WorkflowTestContext) {
    workflowTestContext.finish()
}
