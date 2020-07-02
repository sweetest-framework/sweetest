package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireReal(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.REAL
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = TestEnvironment.dependencies.configurations.getAssignableFrom(clazz)
        TestEnvironment.dependencies.states[dependency].realInitializerUnknown = initializer
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            realInitializerUnknown = initializer
            mode = DependencyMode.REAL
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireMock(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.MOCK
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mockInitializerUnknown = initializer
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            mockInitializerUnknown = initializer
            mode = DependencyMode.MOCK
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireSpy(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.SPY
    }

    private fun getDependencyConfiguration(clazz: KClass<*>) =
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)

    /**
     * Provides an [initializer] for type [clazz] to sweetest.
     * That [initializer] will be used when an instance of [clazz] is needed in the test.
     *
     * Note:
     *  Require functions like [requireReal], [requireMock], etc. cannot be used on a type that uses
     *  [provide]. [provide] automatically requires that the [initializer] is used.
     */
    fun provide(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            providedInitializerUnknown = initializer
            mode = DependencyMode.PROVIDED
        }
    }

    /**
     * Provides an instance of [clazz] to sweetest that is automatically instantiated using the default
     * constructor and the built-in dependency injection.
     *
     * Note:
     *  Require functions like [requireReal], [requireMock], etc. cannot be used on a type that uses
     *  [provide]. [provide] automatically requires that the automatically created instance is used.
     */
    fun provide(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.REAL
    }
}
