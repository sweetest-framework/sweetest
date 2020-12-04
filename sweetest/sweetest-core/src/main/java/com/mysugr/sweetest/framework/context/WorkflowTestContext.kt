package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.workflow.WorkflowStep

class WorkflowTestContext(private val stepsTestContext: StepsTestContext) {

    private var currentStep: WorkflowStep = WorkflowStep.INITIALIZE_FRAMEWORK

    private val supportedSubscriptionSteps = listOf(
        WorkflowStep.INITIALIZE_STEPS,
        WorkflowStep.INITIALIZE_DEPENDENCIES,
        WorkflowStep.BEFORE_SET_UP,
        WorkflowStep.SET_UP,
        WorkflowStep.AFTER_SET_UP,
        WorkflowStep.RUNNING,
        WorkflowStep.TEAR_DOWN,
        WorkflowStep.AFTER_TEAR_DOWN
    )

    private val subscriptionHandlers = supportedSubscriptionSteps.associateWith { mutableListOf<StepHandler>() }

    internal fun subscribe(step: WorkflowStep, handler: () -> Unit) {
        val handlers =
            requireNotNull(subscriptionHandlers[step]) { "Step \"$step\" isn't possible to be subscribed to" }
        require(step > currentStep) { "Can't subscribe to step that was already executed" }
        handlers += StepHandler(handler)
    }

    internal fun run() {
        proceedToInternal(WorkflowStep.RUNNING)
    }

    internal fun finish() {
        proceedToInternal(WorkflowStep.DONE)
    }

    internal fun proceedTo(step: WorkflowStep) {
        require(step != WorkflowStep.DONE) { "Can't proceed to final step from outside the class" }
        proceedToInternal(step)
    }

    private fun proceedToInternal(step: WorkflowStep) {
        require(step != WorkflowStep.INITIALIZE_FRAMEWORK) { "Can't proceed to initial step" }
        require(step > currentStep) { "Can't proceed to step already executed" }
        while (currentStep < step) {
            runStep(currentStep.getNext())
        }
    }

    private fun runStep(step: WorkflowStep) {
        if (step == WorkflowStep.INITIALIZE_DEPENDENCIES) {
            onBeforeInitializeDependencies()
        }
        triggerHandlerFor(step)
        currentStep = step
    }

    private fun onBeforeInitializeDependencies() {
        stepsTestContext.finalizeSetUp()
    }

    private fun triggerHandlerFor(step: WorkflowStep) {
        val stepHandlers = subscriptionHandlers[step] ?: return
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
