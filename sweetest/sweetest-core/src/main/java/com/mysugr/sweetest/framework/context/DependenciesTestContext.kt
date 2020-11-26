package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    internal fun editDependencyState(clazz: KClass<*>, edit: DependencyState<*>.() -> Unit) {
        edit(
            TestEnvironment.dependencies.getDependencyStateForConfiguration(
                clazz = clazz,
                preciseTypeMatching = true
            )
        )
    }

    internal fun editLegacyDependencyState(clazz: KClass<*>, edit: DependencyState<*>.() -> Unit) {
        edit(
            TestEnvironment.dependencies.getDependencyStateForConfiguration(
                clazz = clazz,
                preciseTypeMatching = false
            )
        )
    }

    internal fun checkNotAlreadyProvided(clazz: KClass<*>, mode: DependencyMode) {
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
