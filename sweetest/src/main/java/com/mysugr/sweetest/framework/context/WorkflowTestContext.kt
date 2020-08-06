package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep

class WorkflowTestContext internal constructor(private val steps: StepsTestContext) {

    var currentStep: InitializationStep = InitializationStep.INITIALIZE_FRAMEWORK
        private set

    private val supportedSubscriptionSteps = listOf(
        InitializationStep.INITIALIZE_STEPS,
        InitializationStep.INITIALIZE_DEPENDENCIES,
        InitializationStep.SET_UP,
        InitializationStep.RUNNING,
        InitializationStep.TEAR_DOWN
    )

    private val subscriptionHandlers =
        supportedSubscriptionSteps.associate { it to mutableListOf<StepHandler>() }

    fun subscribe(step: InitializationStep, handler: () -> Unit) {
        val handlers =
            requireNotNull(subscriptionHandlers[step]) { "Step \"$step\" isn't possible to be subscribed to" }
        require(step.isAfter(currentStep)) { "Can't subscribe to step that was already executed" }
        handlers += StepHandler(handler)
    }

    fun run() {
        proceedToInternal(InitializationStep.RUNNING)
    }

    fun finish() {
        proceedToInternal(InitializationStep.DONE)
    }

    fun proceedTo(step: InitializationStep) {
        require(step != InitializationStep.DONE) { "Can't proceed to final step from outside the class" }
        proceedToInternal(step)
    }

    fun proceedToInternal(step: InitializationStep) {
        require(step != InitializationStep.INITIALIZE_FRAMEWORK) { "Can't proceed to initial step" }
        require(step.isAfter(currentStep)) { "Can't proceed to step already executed" }
        while (currentStep.isBefore(step)) {
            runStep(currentStep.getNext())
        }
    }

    private fun runStep(step: InitializationStep) {
        currentStep = step
        if (currentStep == InitializationStep.INITIALIZE_DEPENDENCIES) {
            onBeforeInitializeDependencies()
        }
        triggerHandler(step)
    }

    private fun onBeforeInitializeDependencies() {
        steps.finalizeSetUp()
    }

    private fun triggerHandler(step: InitializationStep) {
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
