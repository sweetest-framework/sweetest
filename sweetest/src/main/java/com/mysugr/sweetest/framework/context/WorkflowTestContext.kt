package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.framework.flow.InitializationStep.DONE
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_DEPENDENCIES
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_FRAMEWORK

class WorkflowTestContext internal constructor(private val steps: StepsTestContext) {

    var currentStep: InitializationStep = INITIALIZE_FRAMEWORK
        private set

    private val stepHandlers = InitializationStep.values().associate { it to mutableListOf<StepHandler>() }

    private fun runStep(step: InitializationStep) {
        currentStep = step
        if (currentStep == INITIALIZE_DEPENDENCIES) {
            onBeforeInitializeDependencies()
        }
        triggerHandler(step)
    }

    private fun onBeforeInitializeDependencies() {
        steps.finalizeSetUp()
    }

    fun proceedTo(step: InitializationStep) {
        if (step.isBeforeOrSame(currentStep)) {
            throw IllegalStateException("The workflow is already at $currentStep, can't proceed to $step")
        } else if (step == DONE || step == INITIALIZE_FRAMEWORK) {
            throw IllegalStateException("It's not allowed to proceed to DONE or INITIALIZE_FRAMEWORK")
        }
        runStep(step)
    }

    fun run() {
        var nextStep = currentStep.getNext()
        while (nextStep != DONE) {
            proceedTo(nextStep)
            nextStep = currentStep.getNext()
        }
    }

    fun subscribe(step: InitializationStep, handler: () -> Unit) {
        if (currentStep.isAfter(step)) {
            throw IllegalStateException("You can't subscribe to a workflow step whose execution is already finished!")
        } else {
            stepHandlers[step]?.add(StepHandler(handler))
        }
    }

    private fun triggerHandler(step: InitializationStep) {
        val stepHandlers = stepHandlers[step]!!
        // Can't use iterator as list is likely to be changed during iteration
        var i = 0
        while (i < stepHandlers.size) {
            stepHandlers[i++].run()
        }
    }

    private data class StepHandler(private val handler: () -> Unit) {
        fun run() = handler()
    }
}
