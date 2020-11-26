package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.framework.flow.WorkflowController
import com.mysugr.sweetest.framework.flow.WorkflowProvider

class WorkflowTestContext(private val steps: StepsTestContext) : WorkflowController,
    WorkflowProvider {

    private var currentStep: InitializationStep = InitializationStep.INITIALIZE_FRAMEWORK

    private val supportedSubscriptionSteps = listOf(
        InitializationStep.INITIALIZE_STEPS,
        InitializationStep.INITIALIZE_DEPENDENCIES,
        InitializationStep.BEFORE_SET_UP,
        InitializationStep.SET_UP,
        InitializationStep.AFTER_SET_UP,
        InitializationStep.RUNNING,
        InitializationStep.TEAR_DOWN,
        InitializationStep.AFTER_TEAR_DOWN
    )

    private val subscriptionHandlers =
        supportedSubscriptionSteps.associate { it to mutableListOf<StepHandler>() }

    override fun subscribe(step: InitializationStep, handler: () -> Unit) {
        val handlers =
            requireNotNull(subscriptionHandlers[step]) { "Step \"$step\" isn't possible to be subscribed to" }
        require(step.isAfter(currentStep)) { "Can't subscribe to step that was already executed" }
        handlers += StepHandler(handler)
    }

    override fun run() {
        proceedToInternal(InitializationStep.RUNNING)
    }

    override fun finish() {
        proceedToInternal(InitializationStep.DONE)
    }

    override fun proceedTo(step: InitializationStep) {
        require(step != InitializationStep.DONE) { "Can't proceed to final step from outside the class" }
        proceedToInternal(step)
    }

    private fun proceedToInternal(step: InitializationStep) {
        require(step != InitializationStep.INITIALIZE_FRAMEWORK) { "Can't proceed to initial step" }
        require(step.isAfter(currentStep)) { "Can't proceed to step already executed" }
        while (currentStep.isBefore(step)) {
            runStep(currentStep.getNext())
        }
    }

    private fun runStep(step: InitializationStep) {
        if (step == InitializationStep.INITIALIZE_DEPENDENCIES) {
            onBeforeInitializeDependencies()
        }
        triggerHandlerFor(step)
        currentStep = step
    }

    private fun onBeforeInitializeDependencies() {
        steps.finalizeSetUp()
    }

    private fun triggerHandlerFor(step: InitializationStep) {
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
