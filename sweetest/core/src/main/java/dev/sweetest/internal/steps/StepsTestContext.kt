package dev.sweetest.internal.steps

import com.mysugr.sweetest.internal.Steps
import dev.sweetest.internal.BDD_INCLUSION_MESSAGE
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.TestContextElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType

class StepsTestContext(private val testContext: TestContext) :
    TestContextElement {

    private val required = mutableListOf<Class<Steps>>()
    private val map = mutableMapOf<Class<*>, Steps>()
    private var setUpDone = false

    private fun checkSetUp(clazz: KClass<*>) {
        check(setUpDone) {
            "You are trying to access steps class \"$clazz\" before all initialization steps had finished. " +
                "Probably you are\n 1) retrieving the steps class outside an appropriate setup code block (e.g. " +
                "`onSetUp { ... }`).\n2) Cucumber hasn't correctly been set up ($BDD_INCLUSION_MESSAGE)."
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

    internal fun finalizeSetUp() {
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

    internal fun <T : Steps> get(clazz: KClass<T>): T {
        checkSetUp(clazz)
        @Suppress("UNCHECKED_CAST")
        return map[clazz.java] as? T ?: create(clazz.java)
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
            checkConstructorExists(kClass)

            // As the TestContext constructor argument is ignored since sweetest v2, pick the constructor without arguments first
            val constructor = getConstructorWithoutArgument(kClass)
                ?: getConstructorWithArgument(kClass)
                ?: error(
                    "Can't use \"${kClass.simpleName}\" as steps class. " +
                        "A steps class needs to have a constructor with no parameters."
                )

            val arguments = if (constructor.parameters.isEmpty()) {
                emptyArray()
            } else {
                arrayOf(testContext)
            }

            val newInstance = constructor.call(*arguments)
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

    private fun <T : Any> getConstructorWithArgument(clazz: KClass<T>): KFunction<T>? {
        return clazz.constructors.find {
            it.parameters.size == 1 && it.parameters.first().type == TestContext::class.starProjectedType
        }
    }

    private fun <T : Any> getConstructorWithoutArgument(clazz: KClass<T>): KFunction<T>? {
        return clazz.constructors.find { it.parameters.isEmpty() }
    }

    private fun checkConstructorExists(clazz: KClass<*>) {
        if (clazz.constructors.isEmpty()) error("Steps class must have at least one constructor")
    }

    internal fun setUpInstance(instance: Steps) {
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

    internal fun setUpAsRequired(kClass: KClass<Steps>) {
        checkNotYetSetUp(kClass)
        val clazz = kClass.java
        if (!required.contains(clazz)) {
            required.add(clazz)
        }
    }

    // Necessary for defining a TestContextElement:

    override val definition = Companion

    companion object : TestContextElement.Definition<StepsTestContext> {
        override fun createInstance(testContext: TestContext) = StepsTestContext(testContext)
    }
}
