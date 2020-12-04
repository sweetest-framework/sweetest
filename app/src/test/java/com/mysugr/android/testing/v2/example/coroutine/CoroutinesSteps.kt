package com.mysugr.android.testing.v2.example.coroutine

import com.mysugr.android.testing.common.example.coroutine.TestDispatcherProvider
import com.mysugr.android.testing.example.coroutine.DispatcherProvider
import dev.sweetest.api.v2.Steps
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
