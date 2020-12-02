package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.TestContextElement
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext : TestContextElement {

    internal fun editDependencyState(dependencyType: KClass<*>, block: DependencyState<*>.() -> Unit) {
        val dependencyState = TestEnvironment.dependencies.getDependencyStateForConfiguration(
            clazz = dependencyType,
            preciseTypeMatching = true
        )
        block(dependencyState)
    }

    internal fun editLegacyDependencyState(dependencyType: KClass<*>, block: DependencyState<*>.() -> Unit) {
        val dependencyState = TestEnvironment.dependencies.getDependencyStateForConfiguration(
            clazz = dependencyType,
            preciseTypeMatching = false
        )
        block(dependencyState)
    }

    internal fun checkNotAlreadyProvided(dependencyType: KClass<*>, mode: DependencyMode) {
        if (mode == DependencyMode.PROVIDED || mode == DependencyMode.AUTO_PROVIDED) {
            throw SweetestException(
                "Dependency \"${dependencyType.simpleName}\" has already been configured with " +
                    "`provide`, it can't be configured again at a different place with " +
                    "`provide<${dependencyType.simpleName}>`. Please eliminate duplicates!\n" +
                    "Reason: configuring the same dependency in different places could lead to ambiguities."
            )
        }
    }

    // Necessary for defining a TestContextElement:
    override val key = Key
    companion object Key : TestContextElement.Key<DependenciesTestContext> {
        override fun createInstance(testContext: TestContext) = DependenciesTestContext()
    }
}
