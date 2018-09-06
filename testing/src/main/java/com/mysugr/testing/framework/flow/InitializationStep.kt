package com.mysugr.testing.framework.flow

enum class InitializationStep(val order: Int) {
    INITIALIZE_FRAMEWORK(0),
    INITIALIZE_STEPS(1),
    INITIALIZE_DEPENDENCIES(2),
    SET_UP(3),
    DONE(4);

    fun isBeforeOrSame(other: InitializationStep) = other.order >= order
    fun isBefore(other: InitializationStep) = other.order > order
    fun isAfter(other: InitializationStep) = other.order < order
    fun isAfterOrSame(other: InitializationStep) = other.order <= order

    fun getNext() = values()[order + 1]
}
