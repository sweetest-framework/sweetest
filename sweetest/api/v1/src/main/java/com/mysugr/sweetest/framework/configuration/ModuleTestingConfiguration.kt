package com.mysugr.sweetest.framework.configuration

import com.mysugr.sweetest.MODULE_CONFIG_DEPRECATION_MESSAGE
import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyProvider
import com.mysugr.sweetest.framework.dependency.DependencySetup
import com.mysugr.sweetest.usecases.ensureEnvironmentInitialized
import kotlin.reflect.KClass

private const val DEPENDENCY_MODE_DEPRECATION_MESSAGE = "Dependency mode constraints " +
    "(\"realOnly\", \"mockOnly\") are obsolete. Use \"any\" instead and add a `provide` " +
    "configuration in a test or steps class instead."

private const val DEPENDENCY_INITIALIZATION_DEPRECATION_MESSAGE = "Dependency initialization in the " +
    "module configuration level is obsolete. Add a `provide` configuration on a test or steps " +
    "class level instead."

@Deprecated(
    "$MODULE_CONFIG_DEPRECATION_MESSAGE. If really necessary because migration " +
        "of test code is not reasonable, only add dependencies with `dependency any of<T>`."
)
fun moduleTestingConfiguration(
    @Suppress("UNUSED_PARAMETER")
    vararg baseModuleTestingConfigurations: ModuleTestingConfiguration, // just a placeholder for making sure necessary base configurations are mentioned
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

    class LeftOperand internal constructor()

    data class RightOperand internal constructor(
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
    internal fun <T : Any> anyInternal(
        @Suppress("UNUSED_PARAMETER")
        leftOperand: LeftOperand, // placeholder for potential future use
        type: KClass<T>
    ) {
        dependencies.add(DependencyConfiguration(type, null, null))
    }

    @PublishedApi
    internal fun anyInternal(
        @Suppress("UNUSED_PARAMETER")
        leftOperand: LeftOperand,  // placeholder for potential future use
        rightOperand: RightOperand
    ) {
        rightOperand.addFunction(null, false)
    }

    @PublishedApi
    internal fun mockOnlyInternal(
        @Suppress("UNUSED_PARAMETER")
        leftOperand: LeftOperand,  // placeholder for potential future use
        rightOperand: RightOperand
    ) {
        rightOperand.addFunction(DependencyMode.MOCK, true)
    }

    @PublishedApi
    internal fun realOnlyInternal(
        @Suppress("UNUSED_PARAMETER")
        leftOperand: LeftOperand,  // placeholder for potential future use
        rightOperand: RightOperand
    ) {
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
                        defaultMockProvider = provider,
                        defaultDependencyMode = finalDependencyMode
                    )
                )
            } else {
                addDependency(
                    DependencyConfiguration(
                        clazz = type,
                        defaultRealProvider = provider,
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
