package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestConfiguration
import com.mysugr.sweetest.framework.coroutine.autoCancelTestCoroutinesEnabled
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoCancelTestCoroutinesConfigurationTest {

    private val sut = CoroutinesTestConfiguration()

    @Test
    fun `autoCancelTestCoroutines is false by default`() {
        assertFalse(sut.data.autoCancelTestCoroutinesEnabled)
    }

    @Test
    fun `autoCancelTestCoroutines is set to true`() {
        sut.autoCancelTestCoroutines(true)
        assertTrue(sut.data.autoCancelTestCoroutinesEnabled)
    }

    @Test
    fun `autoCancelTestCoroutines is set to false`() {
        sut.autoCancelTestCoroutines(false)
        assertFalse(sut.data.autoCancelTestCoroutinesEnabled)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `autoCancelTestCoroutines can't be changed from true to false`() {
        sut.autoCancelTestCoroutines(true)
        sut.autoCancelTestCoroutines(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `autoCancelTestCoroutines can't be changed from false to true`() {
        sut.autoCancelTestCoroutines(false)
        sut.autoCancelTestCoroutines(true)
    }
}