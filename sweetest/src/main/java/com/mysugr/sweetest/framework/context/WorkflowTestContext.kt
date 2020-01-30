package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.framework.flow.InitializationStep.DONE
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_DEPENDENCIES
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_FRAMEWORK
import com.mysugr.sweetest.framework.flow.InitializationStep.RUNNING

class WorkflowTestContext internal constructor(private val steps: StepsTestContext) {

    var currentStep: InitializationStep = INITIALIZE_FRAMEWORK
        private set

    private val stepHandlers =
        InitializationStep.values().associate { it to mutableListOf<StepHandler>() }

    fun subscribe(step: InitializationStep, handler: () -> Unit) {
        if (currentStep.isAfter(step)) {
            throw IllegalStateException("You can't subscribe to a workflow step whose execution is already finished!")
        } else {
            stepHandlers[step]?.add(StepHandler(handler))
        }
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
        runStepsUntil(RUNNING)
    }

    fun finish() {
        runStepsUntil(DONE)
    }

    private fun runStepsUntil(step: InitializationStep) {
        if (currentStep.isAfterOrSame(step)) error("Can't run workflow until $step, it's already at $currentStep")
        while (currentStep.isBefore(step)) {
            runStep(currentStep.getNext())
        }
    }

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
