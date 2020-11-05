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

    fun requireReal(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean) {
        checkInvalidLegacyFunctionCall("requireReal", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.REAL
            }
    }

    fun offerReal(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerReal", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                realInitializerUnknown = initializer
            }
    }

    fun offerRealRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerRealRequired", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.REAL
                realInitializerUnknown = initializer
            }
    }

    fun requireMock(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean) {
        checkInvalidLegacyFunctionCall("requireMock", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.MOCK
            }
    }

    fun offerMock(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerMock", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mockInitializerUnknown = initializer
            }
    }

    fun offerMockRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerMockRequired", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mockInitializerUnknown = initializer
                mode = DependencyMode.MOCK
            }
    }

    fun requireSpy(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean) {
        checkInvalidLegacyFunctionCall("requireSpy", hasModuleTestingConfiguration)
        TestEnvironment.dependencies.getDependencyStateForConfiguration(clazz = clazz, preciseTypeMatching = false)
            .run {
                mode = DependencyMode.SPY
            }
    }

    private fun checkInvalidLegacyFunctionCall(functionName: String, hasModuleTestingConfiguration: Boolean) {
        if (!hasModuleTestingConfiguration) {
            throw SweetestException(
                "`$functionName` is a legacy function and can't be used " +
                    "when using new API without module testing configuration!"
            )
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

    // --- region: legacy binary compatibility API:

    fun requireReal(clazz: KClass<*>) = requireReal(clazz, hasModuleTestingConfiguration = true)

    fun offerReal(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) = offerReal(clazz, initializer, hasModuleTestingConfiguration = true)

    fun offerRealRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) = offerRealRequired(clazz, initializer, hasModuleTestingConfiguration = true)

    fun requireMock(clazz: KClass<*>) = requireMock(clazz, hasModuleTestingConfiguration = true)

    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) =
        offerMock(clazz, initializer, hasModuleTestingConfiguration = true)

    fun offerMockRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) = offerMockRequired(clazz, initializer, hasModuleTestingConfiguration = true)

    fun requireSpy(clazz: KClass<*>) = requireSpy(clazz, hasModuleTestingConfiguration = true)
}
