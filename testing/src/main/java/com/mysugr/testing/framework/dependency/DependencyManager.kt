package com.mysugr.testing.framework.dependency

import com.mysugr.testing.framework.environment.DependencyAccessor
import com.mysugr.testing.framework.environment.DependencySetupHandler
import kotlin.reflect.KClass

class DependencyManager(setupHandlerReceiver: (DependencySetupHandler) -> Unit)
    : DependencyAccessor {

    private var _states: DependencyStates? = null
    private val configurationsField = DependencyConfigurations()

    init {
        setupHandlerReceiver(configurationsField)
    }

    override val configurations: DependencyConfigurationConsumer
        get() = configurationsField

    private val initializerContext = object : DependencyInitializerContext() {
        override fun <T : Any> instanceOf(clazz: KClass<T>): T {
            val configuration = configurations.getAssignableTo(clazz)
            val state = states[configuration]
            return state.instance
        }
    }

    override val states: DependencyStatesConsumer
        get() = if (_states == null) {
            _states = DependencyStates(initializerContext)
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
