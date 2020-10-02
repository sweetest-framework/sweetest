package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.environment.DependencyAccessor
import com.mysugr.sweetest.framework.environment.DependencySetupHandler
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DependencyManager(setupHandlerReceiver: (DependencySetupHandler) -> Unit) : DependencyAccessor {

    private var _states: DependencyStates? = null
    private val configurationsField = DependencyConfigurations()

    init {
        setupHandlerReceiver(configurationsField)
    }

    override val configurations: DependencyConfigurationConsumer
        get() = configurationsField

    private val initializerContext = object : DependencyInitializerContext() {
        override fun <T : Any> instanceOf(clazz: KClass<T>): T {
            return getDependencyState(clazz).instance
        }
    }

    override fun <T : Any> getDependencyState(clazz: KClass<T>): DependencyState<T> {
        if (clazz.isSubclassOf(BaseSteps::class)) {
            throw RuntimeException(
                "Steps classes can't be accessed as dependency, please " +
                    "use the correct function to access steps classes!"
            )
        }

        return with(TestEnvironment.dependencies) {
            states.getOrNull(clazz)
                ?: run {
                    val configuration = configurations.getAssignableTo(clazz)
                        ?: throw SweetestException(
                            "No configuration for \"${clazz.simpleName}\" found! Please configure the type by using " +
                                "`provide<${clazz.simpleName}>...`.\nLegacy note: Adding the type to the module " +
                                "testing configuration also fixes this problem, but these are deprecated. Please use " +
                                "`provide` in your test and steps classes instead!"
                        )
                    if (states.isForcedToPreciseMatching(configuration)) {
                        if (configuration.clazz != clazz) {
                            throw SweetestException( // TODO add extra test case for that
                                "There is a dependency \"${configuration.clazz.simpleName}\" configured in the module " +
                                    "testing configuration, but you are requesting type \"${clazz.simpleName}\". To avoid " +
                                    "ambiguities please specify \"provide<${clazz.simpleName}>...\" explicitly!"
                            )
                        } else {
                            throw SweetestException(
                                "There is a dependency \"${configuration.clazz.simpleName}\" configured in the module " +
                                    "testing configuration, but as there is a chance for ambiguities between " +
                                    "different types you have to specify \"provide<${clazz.simpleName}>...\" " +
                                    "explicitly nonetheless!"
                            )
                        }
                    }
                    states[configuration]
                }
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
