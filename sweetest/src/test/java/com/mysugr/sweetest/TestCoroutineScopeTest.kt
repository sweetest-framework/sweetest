package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.coroutine.coroutineDispatcher
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import com.mysugr.sweetest.framework.coroutine.testCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncompletedCoroutinesError
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor

class TestCoroutineScopeTest {

    class Steps(testContext: TestContext) : BaseSteps(testContext, moduleConfig)

    @Test
    fun `Create TestCoroutineScope`() {
        val test = object : BaseJUnitTest(moduleTestingConfiguration()) {}
        test.testCoroutineScope.smokeTest()
    }

    @Test
    fun `Get CoroutineDispatcher from TestCoroutineScope`() {
        val test = object : BaseJUnitTest(moduleTestingConfiguration()) {}
        val expected = test.testCoroutineScope.coroutineContext[ContinuationInterceptor] as
            CoroutineDispatcher
        val actual = test.coroutineDispatcher
        assertSame(expected, actual)
    }

    @Test
    fun `Just creates one instance (singleton)`() {
        val test = object : BaseJUnitTest(moduleTestingConfiguration()) {}

        assertSame(test.testCoroutineScope, test.testCoroutineScope)
        assertSame(test.coroutineDispatcher, test.coroutineDispatcher)
    }

    @Test
    fun `It doesn't matter whether you get it on the test or steps level`() {
        val test = object : BaseJUnitTest(moduleConfig) {
            val steps by steps<Steps>()
        }

        test.junitBefore()

        assertSame(test.testCoroutineScope, test.steps.testCoroutineScope)
        assertSame(test.coroutineDispatcher, test.steps.coroutineDispatcher)
    }

    // TODO couldn't manage to provoke the wanted exception yet
    @Test(expected = UncompletedCoroutinesError::class)
    fun `Cleans up TestCoroutineScope`() {
        val test = object : BaseJUnitTest(moduleConfig) {}

        test.junitBefore()

        GlobalScope.launch {
            Thread.sleep(1000)
            test.junitAfter()
        }

        CoroutineScope(test.coroutineDispatcher).launch {
            throw Exception("test")
            Thread.sleep(2000)
        }
    }

    @Test(expected = Exception::class)
    fun `Legacy coroutine scope tools don't work together with TestCoroutineScope support`() {
        val test = object : BaseJUnitTest(moduleTestingConfiguration()) {}
        test.testCoroutineScope
        test.testCoroutine { }
    }

    @Test(expected = Exception::class)
    fun `TestCoroutineScope support doesn't work together with legacy coroutine scope tools`() {
        val test = object : BaseJUnitTest(moduleTestingConfiguration()) {}
        test.testCoroutine { }
        test.testCoroutineScope
    }

    private fun TestCoroutineScope.smokeTest() {
        var executed = false
        this.async { executed = true }
        assertTrue(executed)
    }

    companion object {
        private val moduleConfig = moduleTestingConfiguration()
    }
}