package com.mysugr.sweetest.framework.workflow

enum class WorkflowStep {

    INITIALIZE_FRAMEWORK,
    INITIALIZE_STEPS,
    INITIALIZE_DEPENDENCIES,
    BEFORE_SET_UP,
    SET_UP,
    AFTER_SET_UP,
    RUNNING,
    TEAR_DOWN,
    AFTER_TEAR_DOWN,
    DONE;

    fun getNext() = values()[ordinal + 1]
}
