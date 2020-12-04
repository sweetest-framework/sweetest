package dev.sweetest.demo.v1.example.coroutine

import dev.sweetest.demo.common.TestDispatcherProvider
import dev.sweetest.demo.coroutine.DispatcherProvider
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
