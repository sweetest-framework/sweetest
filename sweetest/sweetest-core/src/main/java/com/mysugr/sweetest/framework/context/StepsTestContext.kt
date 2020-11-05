package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.internal.TestContext
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

interface StepsProvider {
    fun <T : Steps> getOf(clazz: Class<T>): T
}

class StepsTestContext(private val testContext: TestContext) {

    private val required = mutableListOf<Class<Steps>>()
    private val map = mutableMapOf<Class<*>, Steps>()
    private var setUpDone = false

    private fun checkSetUp(clazz: KClass<*>) {
        check(setUpDone) {
            "You are trying to access steps class \"$clazz\" before the all initialization steps had finished." +
                "Probably you are retrieving the steps class outside an appropriate setup code block which " +
                "should be registered in a steps or test class with `override fun configure() = " +
                "super.configure().onSetUp { ... }`."
        }
    }

    private fun checkNotYetSetUp(clazz: KClass<*>) {
        check(!setUpDone) {
            "You are trying to access steps class \"$clazz\" which has not " +
                "yet been initialized. That can happen when you are using a BDD framework and it's not " +
                "initializing the steps class before testing starts. You can fix that by adding a dummy function " +
                "in the steps class with a @Before annotation (this forces the class to be instantiated earlier)."
        }
    }

    fun finalizeSetUp() {
        if (!setUpDone) {
            // Not using iterator as list is possibly enlarged during iteration, which would cause an exception
            var i = 0
            while (i < required.size) {
                forceInitializationOf(required[i++])
            }
            setUpDone = true
        }
    }

    private fun <T : Steps> forceInitializationOf(clazz: Class<T>) {
        map[clazz] ?: create(clazz)
    }

    fun <T : Steps> get(clazz: KClass<T>): T {
        checkSetUp(clazz)
        @Suppress("UNCHECKED_CAST")
        return map[clazz.java] as? T ?: create(clazz.java)
    }

    val provider = object : StepsProvider {
        override fun <T : Steps> getOf(clazz: Class<T>) = get(clazz.kotlin)
    }

    private fun <T : Steps> create(clazz: Class<T>): T {
        val kClass = clazz.kotlin
        return try {
            if (!required.contains<Class<*>>(clazz)) {
                throw RuntimeException(
                    "Steps class \"$clazz\" has not yet been marked as required! Each steps class " +
                        "has to be set up as required before it's used!"
                )
            }
            checkType(kClass)
            checkConstructor(kClass)
            val newInstance = kClass.constructors.first().call(testContext)
            map[clazz] = newInstance
            newInstance
        } catch (exception: Exception) {
            throw RuntimeException(
                "Could not automatically create steps class \"$clazz\". Either fix the underlying " +
                    "cause (see nested exception), instantiate it manually so it will be added to the list of " +
                    "steps classes or make sure Cucumber instantiated the steps class!",
                exception
            )
        }
    }

    private fun checkType(clazz: KClass<*>) {
        if (!clazz.isSubclassOf(Steps::class)) {
            throw RuntimeException("\"$clazz\", as all steps classes, should derive from \"BaseNewSteps\"")
        }
    }

    private fun checkConstructor(clazz: KClass<*>) {
        try {
            val constructors = clazz.constructors
            if (constructors.size > 1) {
                throw RuntimeException("There is more than one constructor")
            }
            val constructor = constructors.first()
            if (constructor.parameters.size > 1 ||
                !constructor.parameters.first().type.isSubtypeOf(TestContext::class.starProjectedType)
            ) {
                throw RuntimeException("Wrong constructor")
            }
        } catch (exception: Exception) {
            throw RuntimeException(
                "\"$clazz\", as all steps classes which you want to auto-instanciate, should " +
                    "have exactly one constructor receiving a TestContext object!",
                exception
            )
        }
    }

    fun setUpInstance(instance: Steps) {
        checkNotYetSetUp(instance::class)
        checkType(instance::class)
        val clazz = instance::class.java
        if (map.containsKey(clazz)) {
            throw RuntimeException(
                "An instance of steps class \"${instance.javaClass}\" has already been " +
                    "registered! If you run under Cucumber make sure the steps class is initialized on time. " +
                    "You can force initialization by adding a @Before-annotated function to the class."
            )
        }
        map[clazz] = instance
    }

    fun setUpAsRequired(kClass: KClass<Steps>) {
        checkNotYetSetUp(kClass)
        val clazz = kClass.java
        if (!required.contains(clazz)) {
            required.add(clazz)
        }
    }

    class GetStepsClassException(clazz: KClass<*>) : RuntimeException(
        "Could not get steps class of type \"$clazz\": " +
            "it has not yet been registered. Are you sure you have created an instance of the steps class before? Is " +
            "it created by Cucumber? Have you used the correct base class \"BaseNewSteps\" for the steps class?"
    )
}
