package dev.sweetest.demo.v2.example.coroutine

import dev.sweetest.demo.common.TestDispatcherProvider
import dev.sweetest.demo.coroutine.DispatcherProvider
import dev.sweetest.v2.Steps
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.ContinuationInterceptor

class CoroutinesSteps : Steps() {

    init {
        provide<TestCoroutineScope> { TestCoroutineScope() }
        provide<CoroutineScope> { instanceOf<TestCoroutineScope>() }
        provide<CoroutineDispatcher> {
            instanceOf<CoroutineScope>().coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        }
        provide<TestDispatcherProvider>()
        provide<DispatcherProvider> { instanceOf<TestDispatcherProvider>() }
    }
}
