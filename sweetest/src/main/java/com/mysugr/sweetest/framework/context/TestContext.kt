package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.framework.coroutine.LegacyCoroutinesTestContext
import com.mysugr.sweetest.framework.environment.TestEnvironment

class TestContext internal constructor() {

    @PublishedApi
    internal val steps = StepsTestContext(this)

    @PublishedApi
    internal val factories = FactoriesTestContext(this)

    @PublishedApi
    internal val configurations = ConfigurationsTestContext(factories)

    @PublishedApi
    internal val dependencies = DependenciesTestContext()

    val workflow = WorkflowTestContext(steps)

    private var _legacyCoroutines: LegacyCoroutinesTestContext? = null
    internal val legacyCoroutines: LegacyCoroutinesTestContext
        get() = getLegacyCoroutinesTestContext()

    private var _coroutines: CoroutinesTestContext? = null
    internal val coroutines: CoroutinesTestContext
        get() = getCoroutinesTestContext()

    init {
        TestEnvironment.reset()
    }

    private fun getLegacyCoroutinesTestContext(): LegacyCoroutinesTestContext {
        synchronized(this) {
            check(_coroutines == null) { coroutinesLegacyErrorMessage }
            if (_legacyCoroutines == null) {
                _legacyCoroutines = LegacyCoroutinesTestContext()
            }
            return _legacyCoroutines!!
        }
    }

    private fun getCoroutinesTestContext(): CoroutinesTestContext {
        synchronized(this) {
            check(_legacyCoroutines == null) { coroutinesLegacyErrorMessage }
            if (_coroutines == null) {
                _coroutines = CoroutinesTestContext(workflow)
            }
            return _coroutines!!
        }
    }

    companion object {
        val coroutinesLegacyErrorMessage: String = """
        Can't use legacy and new coroutines tools side-by-side! 
        
        Legacy:
        - BaseJUnitTest.testCoroutine { ... }
        - BaseSteps.coroutineContext (using steps class as CoroutineScope)
        
        New:
        - BaseJUnitTest.testCoroutineScope
        - BaseJUnitTest.coroutineDispatcher
        - BaseSteps.testCoroutineScope
        - BaseSteps.coroutineDispatcher
        """.trimIndent()
    }
}
