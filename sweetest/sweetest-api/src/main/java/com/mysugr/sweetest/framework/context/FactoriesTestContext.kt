package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.factory.FactoryRunner
import kotlin.reflect.KClass

class FactoriesTestContext(private val parent: TestContext) {

    @PublishedApi
    internal val map = mutableMapOf<Class<*>, FactoryRunner<*>>()

    @PublishedApi
    internal fun configure(runner: FactoryRunner<*>) {
        if (map.containsKey(runner.returnType)) {
            throw RuntimeException(
                "Factory for type \"${runner.returnType}\" is already registered! " +
                    "Please remove all redundant configurations in your module test configurations or " +
                    "steps classes!"
            )
        }
        map[runner.returnType] = runner
    }

    @PublishedApi
    internal fun <R : Any> get(type: KClass<R>): FactoryRunner<R> =
        map[type.java] as? FactoryRunner<R>
            ?: throw RuntimeException(
                "Factory for type \"$type\" is not configured! Please make sure " +
                    "you have configured it in your module test configuration\" or in a steps class and the " +
                    "steps class has been instantiated!"
            )
}
