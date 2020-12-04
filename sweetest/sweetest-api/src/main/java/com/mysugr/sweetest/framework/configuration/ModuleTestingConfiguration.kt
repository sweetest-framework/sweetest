package com.mysugr.sweetest.framework.configuration

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyProvider
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencySetup
import com.mysugr.sweetest.framework.dependency.asCoreDependencyProvider
import com.mysugr.sweetest.usecases.ensureEnvironmentInitialized
import kotlin.reflect.KClass

private const val DEPENDENCY_MODE_DEPRECATION_MESSAGE = "Dependency mode constraints " +
    "(\"realOnly\", \"mockOnly\") are obsolete. Use \"any\" instead and add a `provide` " +
    "configuration in a test or steps class instead."

private const val DEPENDENCY_INITIALIZATION_DEPRECATION_MESSAGE = "Dependency initialization in the " +
    "module configuration level is obsolete. Add a `provide` configuration on a test or steps " +
    "class level instead."

fun moduleTestingConfiguration(
    vararg baseModuleTestingConfigurations: ModuleTestingConfiguration,
    run: (DslScope.() -> Unit)? = null
): ModuleTestingConfiguration {

    // Force initialization before everything else
    ensureEnvironmentInitialized()

    val scope = DslScope()
    run?.invoke(scope)

    return ModuleTestingConfiguration()
}

// Placeholder class, as data is actually globally stored instead of in this data structure
class ModuleTestingConfiguration internal constructor()

class DslScope internal constructor() {

    private val dependencies = mutableListOf<DependencyConfiguration<*>>()

    private fun addDependency(configuration: DependencyConfiguration<*>) {
        dependencies.add(configuration)
        DependencySetup.addConfiguration(configuration)
    }

    // Dependency configuration

    class LeftOperand

    data class RightOperand(
        internal val addFunction: (dependencyMode: DependencyMode?, only: Boolean) -> Unit
    )

    val dependency = LeftOperand()

    inline infix fun <reified T : Any> LeftOperand.any(dependencyType: KClass<T>) {
        anyInternal(this, dependencyType)
    }

    infix fun LeftOperand.any(rightOperand: RightOperand) {
        anyInternal(this, rightOperand)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE)
    infix fun LeftOperand.mockOnly(rightOperand: RightOperand) {
        mockOnlyInternal(this, rightOperand)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE)
    infix fun LeftOperand.realOnly(rightOperand: RightOperand) {
        realOnlyInternal(this, rightOperand)
    }

    @Deprecated(DEPENDENCY_INITIALIZATION_DEPRECATION_MESSAGE)
    inline fun <reified T : Any> initializer(noinline provider: DependencyProvider<T>): RightOperand =
        initializerInternal(T::class, provider)

    inline fun <reified T : Any> of(): RightOperand =
        ofInternal(T::class)

    // Events

    fun onInitialization(run: () -> Unit) = run()

    // Internal API

    @PublishedApi
    internal fun <T : Any> anyInternal(leftOperand: LeftOperand, type: KClass<T>) {
        dependencies.add(DependencyConfiguration(type, null, null))
    }

    @PublishedApi
    internal fun anyInternal(leftOperand: LeftOperand, rightOperand: RightOperand) {
        rightOperand.addFunction(null, false)
    }

    @PublishedApi
    internal fun mockOnlyInternal(leftOperand: LeftOperand, rightOperand: RightOperand) {
        rightOperand.addFunction(DependencyMode.MOCK, true)
    }

    @PublishedApi
    internal fun realOnlyInternal(leftOperand: LeftOperand, rightOperand: RightOperand) {
        rightOperand.addFunction(DependencyMode.REAL, true)
    }

    @PublishedApi
    internal fun <T : Any> initializerInternal(type: KClass<T>, provider: DependencyProvider<T>) =
        RightOperand { dependencyMode, only ->
            val finalDependencyMode = if (only) dependencyMode else null
            if (dependencyMode == DependencyMode.MOCK) {
                addDependency(
                    DependencyConfiguration(
                        clazz = type,
                        defaultRealProvider = null,
                        defaultMockProvider = provider.asCoreDependencyProvider(),
                        defaultDependencyMode = finalDependencyMode
                    )
                )
            } else {
                addDependency(
                    DependencyConfiguration(
                        clazz = type,
                        defaultRealProvider = provider.asCoreDependencyProvider(),
                        defaultMockProvider = null,
                        defaultDependencyMode = finalDependencyMode
                    )
                )
            }
        }

    @PublishedApi
    internal fun <T : Any> ofInternal(type: KClass<T>) =
        RightOperand { dependencyMode, only ->
            val finalDependencyMode = if (only) dependencyMode else null
            addDependency(DependencyConfiguration(type, null, null, finalDependencyMode))
        }
}
