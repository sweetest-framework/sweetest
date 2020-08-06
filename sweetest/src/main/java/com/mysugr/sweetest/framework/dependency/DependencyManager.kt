package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.environment.DependencyAccessor
import com.mysugr.sweetest.framework.environment.DependencySetupHandler
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DependencyManager(setupHandlerReceiver: (DependencySetupHandler) -> Unit) : DependencyAccessor() {

    private var _states: DependencyStates? = null
    private val configurationsField = DependencyConfigurations()

    init {
        setupHandlerReceiver(configurationsField)
    }

    override val configurations: DependencyConfigurationConsumer
        get() = configurationsField

    override fun <T : Any> instanceOf(clazz: KClass<T>): T {
        try {
            return getDependencyState(clazz).instance
        } catch (throwable: Throwable) {
            throw RuntimeException(
                "Call on \"dependency<${clazz.simpleName}>\" failed",
                throwable
            )
        }
    }

    override fun <T : Any> getDependencyState(clazz: KClass<T>): DependencyState<T> {
        if (clazz.isSubclassOf(BaseSteps::class)) {
            throw RuntimeException(
                "Steps classes can't be accessed as dependency, please " +
                    "use the correct function to access steps classes!"
            )
        }

        return TestEnvironment.dependencies.configurations.getAssignableTo(clazz)?.let {
            TestEnvironment.dependencies.states.getByConfiguration(it)
        } ?: run {
            TestEnvironment.dependencies.states.getByDependencyType(clazz)
        } ?: error(
            "No configuration or dependency state for ${clazz.simpleName} added. " +
                "Please specify it explicitly."
        )
    }

    override val states: DependencyStatesConsumer
        get() = if (_states == null) {
            _states = DependencyStates(this)
            _states!!
        } else {
            _states!!
        }

    class Controller(private val parent: DependencyManager) {
        fun resetState() {
            parent._states = null
        }
    }
}
