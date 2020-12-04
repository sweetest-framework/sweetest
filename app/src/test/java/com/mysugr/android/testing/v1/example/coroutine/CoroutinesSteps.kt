package com.mysugr.android.testing.v1.example.coroutine

import com.mysugr.android.testing.common.example.coroutine.TestDispatcherProvider
import com.mysugr.android.testing.example.coroutine.DispatcherProvider
import com.mysugr.sweetest.framework.base.BaseSteps
import dev.sweetest.internal.TestContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.ContinuationInterceptor

class CoroutinesSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .provide<TestCoroutineScope> { TestCoroutineScope() }
        .provide<CoroutineScope> { instanceOf<TestCoroutineScope>() }
        .provide<CoroutineDispatcher> {
            instanceOf<CoroutineScope>().coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        }
        .provide<TestDispatcherProvider>()
        .provide<DispatcherProvider> { instanceOf<TestDispatcherProvider>() }
}
