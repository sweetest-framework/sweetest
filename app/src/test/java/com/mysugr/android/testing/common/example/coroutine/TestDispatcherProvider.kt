package com.mysugr.android.testing.common.example.coroutine

import com.mysugr.android.testing.example.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(coroutineDispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val default = coroutineDispatcher
    override val io = coroutineDispatcher
    override val main = coroutineDispatcher
    override val unconfined = coroutineDispatcher
}
