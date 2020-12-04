package dev.sweetest.v2

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.usecases.getCurrentTestContext
import com.mysugr.sweetest.usecases.proceedWorkflow
import dev.sweetest.api.v2.BaseTest

fun BaseTest.startWorkflow() {
    proceedWorkflow(getCurrentTestContext()[WorkflowTestContext])
}

fun BaseTest.finishWorkflow() {
    com.mysugr.sweetest.usecases.finishWorkflow(getCurrentTestContext()[WorkflowTestContext])
}
