package com.mysugr.testing.framework.configuration

import com.mysugr.testing.framework.base.Steps
import com.mysugr.testing.framework.dependency.DependencyConfiguration
import com.mysugr.testing.framework.dependency.DependencyInitializer
import com.mysugr.testing.framework.dependency.DependencyMode
import com.mysugr.testing.framework.environment.TestEnvironment
import com.mysugr.testing.framework.factory.FactoryRunner
import com.mysugr.testing.framework.factory.FactoryRunner0
import com.mysugr.testing.framework.factory.FactoryRunner1
import com.mysugr.testing.framework.factory.FactoryRunner2
import com.mysugr.testing.framework.factory.FactoryRunner3
import kotlin.reflect.KClass

fun moduleTestingConfiguration(
    vararg baseModuleTestingConfigurations: ModuleTestingConfiguration,
    run: (Dsl.MainScope.() -> Unit)? = null
): ModuleTestingConfiguration {

    // Force initialization before everything else
    TestEnvironment

    val scope = Dsl.MainScope()
    run?.invoke(scope)

    return ModuleTestingConfiguration(baseModuleTestingConfigurations.toList(), scope.factories.toList())
}

data class ModuleTestingConfiguration internal constructor(
    internal val baseModuleTestingConfigurations: List<ModuleTestingConfiguration>,
    internal val factories: List<FactoryRunner<*>>
)

class Dsl {

    class MainScope {

        @PublishedApi
        internal val factories = mutableListOf<FactoryRunner<*>>()

        @PublishedApi
        internal val dependencies = mutableListOf<DependencyConfiguration<*>>()

        @PublishedApi
        internal fun addDependency(configuration: DependencyConfiguration<*>) {
            dependencies.add(configuration)
            com.mysugr.testing.framework.dependency.DependencySetup.addConfiguration(configuration)
        }

        // Supplier configuration

        inline fun <reified R : Any> factory(noinline createObject: () -> R) {
            factories.add(FactoryRunner0(R::class.java, createObject))
        }

        inline fun <reified T : Steps, reified R : Any> factory(noinline createObject: (T) -> R) {
            factories.add(FactoryRunner1(R::class.java, T::class.java, createObject))
        }

        inline fun <reified T1 : Steps, reified T2 : Steps, reified R : Any> factory(
            noinline createObject: (T1, T2) -> R
        ) {
            factories.add(FactoryRunner2(R::class.java, T1::class.java, T2::class.java, createObject))
        }

        inline fun <reified T1 : Steps, reified T2 : Steps, reified T3 : Steps, reified R : Any> factory(
            noinline createObject: (T1, T2, T3) -> R
        ) {
            factories.add(FactoryRunner3(R::class.java, T1::class.java, T2::class.java, T3::class.java,
                createObject))
        }

        // Dependency configuration

        class LeftOperand

        data class RightOperand(
            @PublishedApi internal val addFunction: (dependencyMode: DependencyMode?, only: Boolean) -> Unit
        )

        data class AutoInitializerRightOperand(
            @PublishedApi internal val addFunction: () -> Unit
        )

        val dependency = LeftOperand()

        inline infix fun <reified T : Any> LeftOperand.any(clazz: KClass<T>) {
            dependencies.add(DependencyConfiguration(clazz, null, null))
        }

        infix fun LeftOperand.any(rightOperand: RightOperand) {
            rightOperand.addFunction(null, false)
        }

        infix fun LeftOperand.mockOnly(rightOperand: RightOperand) {
            rightOperand.addFunction(DependencyMode.MOCK, true)
        }

        infix fun LeftOperand.realOnly(rightOperand: RightOperand) {
            rightOperand.addFunction(DependencyMode.REAL, true)
        }

        inline fun <reified T : Any> initializer(noinline initializer: DependencyInitializer<T>) =
            RightOperand { dependencyMode, only ->
                val finalDependencyMode = if (only) dependencyMode else null
                if (dependencyMode == DependencyMode.MOCK) {
                    addDependency(DependencyConfiguration(T::class, null, initializer, finalDependencyMode))
                } else {
                    addDependency(DependencyConfiguration(T::class, initializer, null, finalDependencyMode))
                }
            }

        inline fun <reified T : Any> of() =
            RightOperand { dependencyMode, only ->
                val finalDependencyMode = if (only) dependencyMode else null
                addDependency(DependencyConfiguration(T::class, null, null, finalDependencyMode))
            }

        inline fun <reified T : Any> instance(instance: T) =
            RightOperand { dependencyMode, only ->
                val finalDependencyMode = if (only) dependencyMode else null
                when (dependencyMode) {
                    DependencyMode.MOCK ->
                        addDependency(DependencyConfiguration(T::class, null, { instance }, finalDependencyMode))
                    else ->
                        addDependency(DependencyConfiguration(T::class, { instance }, null, finalDependencyMode))
                }
            }

        // Events

        fun onInitialization(run: () -> Unit) = run()
    }
}