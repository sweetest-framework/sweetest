package com.mysugr.sweetest.framework.factory

import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.framework.context.StepsProvider

class FactoryRunner0<R : Any>(
    returnType: Class<R>,
    private val run: () -> R
) : FactoryRunner<R>(returnType) {

    override fun run(stepsProvider: StepsProvider): R {
        return run()
    }
}

class FactoryRunner1<T : Steps, R : Any>(
    returnType: Class<R>,
    private val parameterType: Class<T>,
    private val run: (T) -> R
) : FactoryRunner<R>(returnType) {

    override fun run(stepsProvider: StepsProvider): R {
        val argument = stepsProvider.getOf(parameterType)
        return run(argument)
    }
}

class FactoryRunner2<T1 : Steps, T2 : Steps, R : Any>(
    returnType: Class<R>,
    private val parameterType1: Class<T1>,
    private val parameterType2: Class<T2>,
    private val run: (T1, T2) -> R
) : FactoryRunner<R>(returnType) {

    override fun run(stepsProvider: StepsProvider): R {
        val argument1 = stepsProvider.getOf(parameterType1)
        val argument2 = stepsProvider.getOf(parameterType2)
        return run(argument1, argument2)
    }
}

class FactoryRunner3<T1 : Steps, T2 : Steps, T3 : Steps, R : Any>(
    returnType: Class<R>,
    private val parameterType1: Class<T1>,
    private val parameterType2: Class<T2>,
    private val parameterType3: Class<T3>,
    private val run: (T1, T2, T3) -> R
) : FactoryRunner<R>(returnType) {

    override fun run(stepsProvider: StepsProvider): R {
        val argument1 = stepsProvider.getOf(parameterType1)
        val argument2 = stepsProvider.getOf(parameterType2)
        val argument3 = stepsProvider.getOf(parameterType3)
        return run(argument1, argument2, argument3)
    }
}

abstract class FactoryRunner<R : Any>(val returnType: Class<R>) {
    abstract fun run(stepsProvider: StepsProvider): R
}
