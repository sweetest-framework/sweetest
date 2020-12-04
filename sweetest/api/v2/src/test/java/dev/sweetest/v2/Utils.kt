package dev.sweetest.v2

import dev.sweetest.internal.environment.getCurrentTestContext
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.finishWorkflow
import dev.sweetest.internal.workflow.proceedWorkflow
import dev.sweetest.v2.internal.ApiTest

fun ApiTest.startWorkflow() {
    proceedWorkflow(getCurrentTestContext()[WorkflowTestContext])
}

fun ApiTest.finishWorkflow() {
    finishWorkflow(getCurrentTestContext()[WorkflowTestContext])
}
