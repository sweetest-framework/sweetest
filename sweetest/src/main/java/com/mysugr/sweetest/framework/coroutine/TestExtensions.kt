package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.Steps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun BaseJUnitTest.testCoroutine(
        testBlock: suspend CoroutineScope.() -> Unit
) {
    runBlocking {
        val coroutinesTestContext = accessor.testContext.coroutines
        withContext(coroutinesTestContext.coroutineContext) {
            testBlock()
        }
        coroutinesTestContext.testFinished()
    }
}

suspend operator fun <T : Steps> T.invoke(run: suspend T.() -> Unit) = run(this)

suspend fun Deferred<*>.throwExceptionIfFailed() {
    if (isCompleted) {
        await() // throws exception, if Deferred failed. Does nothing otherwise
    }
}