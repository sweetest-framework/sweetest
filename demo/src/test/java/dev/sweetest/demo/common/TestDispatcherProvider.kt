package dev.sweetest.demo.common

import dev.sweetest.demo.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(coroutineDispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val default = coroutineDispatcher
    override val io = coroutineDispatcher
    override val main = coroutineDispatcher
    override val unconfined = coroutineDispatcher
}
