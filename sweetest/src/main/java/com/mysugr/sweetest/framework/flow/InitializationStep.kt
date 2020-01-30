package com.mysugr.sweetest.framework.flow

enum class InitializationStep(val order: Int) {
    INITIALIZE_FRAMEWORK(0),
    INITIALIZE_STEPS(1),
    INITIALIZE_DEPENDENCIES(2),
    SET_UP(3),
    RUNNING(4),
    TEAR_DOWN(5),
    DONE(6);

    fun isBeforeOrSame(other: InitializationStep) = other.order >= order
    fun isBefore(other: InitializationStep) = other.order > order
    fun isAfter(other: InitializationStep) = other.order < order
    fun isAfterOrSame(other: InitializationStep) = other.order <= order

    fun getNext() = values()[order + 1]
}
