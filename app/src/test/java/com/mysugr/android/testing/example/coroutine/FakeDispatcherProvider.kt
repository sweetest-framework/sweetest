package com.mysugr.android.testing.example.coroutine

import kotlinx.coroutines.CoroutineDispatcher

class FakeDispatcherProvider(coroutineDispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val default = coroutineDispatcher
    override val io = coroutineDispatcher
    override val main = coroutineDispatcher
    override val unconfined = coroutineDispatcher
}
