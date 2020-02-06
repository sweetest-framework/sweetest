package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestConfiguration
import com.mysugr.sweetest.framework.coroutine.autoSetMainCoroutineDispatcherEnabled
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoSetMainDispatcherConfigurationTest {

    private val sut = CoroutinesTestConfiguration()

    @Test
    fun `autoSetMainCoroutineDispatcher is true by default`() {
        assertTrue(sut.data.autoSetMainCoroutineDispatcherEnabled)
    }

    @Test
    fun `autoSetMainCoroutineDispatcher is set to true`() {
        sut.autoSetMainCoroutineDispatcher(true)
        assertTrue(sut.data.autoSetMainCoroutineDispatcherEnabled)
    }

    @Test
    fun `autoSetMainCoroutineDispatcher is set to false`() {
        sut.autoSetMainCoroutineDispatcher(false)
        assertFalse(sut.data.autoSetMainCoroutineDispatcherEnabled)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `autoSetMainCoroutineDispatcher can't be changed from true to false`() {
        sut.autoSetMainCoroutineDispatcher(true)
        sut.autoSetMainCoroutineDispatcher(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `autoSetMainCoroutineDispatcher can't be changed from false to true`() {
        sut.autoSetMainCoroutineDispatcher(false)
        sut.autoSetMainCoroutineDispatcher(true)
    }
}