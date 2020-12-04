package dev.sweetest.v2

import dev.sweetest.internal.environment.getCurrentTestContext
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.finishWorkflow
import dev.sweetest.internal.workflow.proceedWorkflow

fun BaseTest.startWorkflow() {
    proceedWorkflow(getCurrentTestContext()[WorkflowTestContext])
}

fun BaseTest.finishWorkflow() {
    finishWorkflow(getCurrentTestContext()[WorkflowTestContext])
}
