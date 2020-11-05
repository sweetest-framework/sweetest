package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.accessor.BaseAccessor

interface TestingAccessor {
    val accessor: BaseAccessor
}

inline fun <reified T : Any> TestingAccessor.dependency() = accessor.delegates.dependency<T>()
inline fun <reified T : Steps> TestingAccessor.steps() = accessor.delegates.steps<T>()
inline fun <reified T : Any> TestingAccessor.factory() = accessor.delegates.factory<T>()
