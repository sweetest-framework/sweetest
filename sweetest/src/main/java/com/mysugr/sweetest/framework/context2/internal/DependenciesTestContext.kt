package com.mysugr.sweetest.framework.context2.internal

import com.mysugr.sweetest.framework.dependency2.DependencyRetriever
import com.mysugr.sweetest.framework.dependency2.DependencyStateStore

internal class DependenciesTestContext {
    val states = DependencyStateStore()
    val retriever = DependencyRetriever(states)
}