package com.mysugr.sweetest.framework.flow

enum class InitializationStep {

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

    fun isBeforeOrSame(other: InitializationStep) = other.ordinal >= ordinal
    fun isBefore(other: InitializationStep) = other.ordinal > ordinal
    fun isAfter(other: InitializationStep) = other.ordinal < ordinal
    fun isAfterOrSame(other: InitializationStep) = other.ordinal <= ordinal

    fun getNext() = values()[ordinal + 1]
}
