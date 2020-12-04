package dev.sweetest.v2

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.getCurrentTestContext
import com.mysugr.sweetest.usecases.proceedWorkflow

fun BaseTest.startWorkflow() {
    proceedWorkflow(getCurrentTestContext()[WorkflowTestContext])
}

fun BaseTest.finishWorkflow() {
    finishWorkflow(getCurrentTestContext()[WorkflowTestContext])
}
