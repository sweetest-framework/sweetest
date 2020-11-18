package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    fun provide(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = true).run {
            checkNotAlreadyProvided(clazz, mode)
            mode = DependencyMode.PROVIDED
            providedInitializerUnknown = initializer
        }
    }

    fun provide(clazz: KClass<*>) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = true).run {
            checkNotAlreadyProvided(clazz, mode)
            mode = DependencyMode.AUTO_PROVIDED
        }
    }

    fun requireReal(clazz: KClass<*>) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.REAL
            }
    }

    fun offerReal(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                realInitializerUnknown = initializer
            }
    }

    fun offerRealRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.REAL
                realInitializerUnknown = initializer
            }
    }

    fun requireMock(clazz: KClass<*>) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.MOCK
            }
    }

    fun offerMock(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mockInitializerUnknown = initializer
            }
    }

    fun offerMockRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mockInitializerUnknown = initializer
                mode = DependencyMode.MOCK
            }
    }

    fun requireSpy(clazz: KClass<*>) {
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.SPY
            }
    }

    private fun checkNotAlreadyProvided(clazz: KClass<*>, mode: DependencyMode) {
        if (mode == DependencyMode.PROVIDED || mode == DependencyMode.AUTO_PROVIDED) {
            throw SweetestException(
                "Dependency \"${clazz.simpleName}\" has already been configured with " +
                    "`provide`, it can't be configured again at a different place with " +
                    "`provide<${clazz.simpleName}>`. Please eliminate duplicates!\n" +
                    "Reason: configuring the same dependency in different places could lead to ambiguities."
            )
        }
    }
}
