package com.mysugr.android.testing.example.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.ContinuationInterceptor

class FakeDispatcherProvider(coroutineDispatcher: CoroutineDispatcher) : DispatcherProvider {

    constructor(coroutineScope: CoroutineScope) :
        this(coroutineScope.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)

    override val default = coroutineDispatcher
    override val io = coroutineDispatcher
    override val main = coroutineDispatcher
    override val unconfined = coroutineDispatcher
}
