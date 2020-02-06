package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestConfiguration
import com.mysugr.sweetest.framework.coroutine.useLegacyTestCoroutineEnabled
import com.mysugr.sweetest.framework.coroutine.useLegacyTestCoroutineEnabled
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UseLegacyCoroutineScopeConfigurationTest {

    private val sut = CoroutinesTestConfiguration()

    @Test
    fun `useLegacyTestCoroutine is false by default`() {
        assertFalse(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test
    fun `useLegacyTestCoroutine is set to true`() {
        sut.useLegacyCoroutineScopeOnTestLevel(true)
        assertTrue(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test
    fun `useLegacyTestCoroutine is set to false`() {
        sut.useLegacyCoroutineScopeOnTestLevel(false)
        assertFalse(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test
    fun `useLegacyTestCoroutine is set to false two times on test level`() {
        sut.useLegacyCoroutineScopeOnTestLevel(false)
        sut.useLegacyCoroutineScopeOnTestLevel(false)
        assertFalse(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test
    fun `useLegacyTestCoroutine is set to true two times on test level`() {
        sut.useLegacyCoroutineScopeOnTestLevel(true)
        sut.useLegacyCoroutineScopeOnTestLevel(true)
        assertTrue(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test
    fun `useLegacyTestCoroutine is set to false two times on different levels`() {
        sut.useLegacyCoroutineScopeOnTestLevel(false)
        sut.useLegacyCoroutineScopeOnStepsLevel(false)
        assertFalse(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test
    fun `useLegacyTestCoroutine is set to true two times on different levels`() {
        sut.useLegacyCoroutineScopeOnTestLevel(true)
        sut.useLegacyCoroutineScopeOnStepsLevel(true)
        assertTrue(sut.data.useLegacyTestCoroutineEnabled)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `useLegacyTestCoroutine can't be changed from true to false on test`() {
        sut.useLegacyCoroutineScopeOnTestLevel(true)
        sut.useLegacyCoroutineScopeOnTestLevel(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `useLegacyTestCoroutine can't be changed from false to true on test`() {
        sut.useLegacyCoroutineScopeOnTestLevel(false)
        sut.useLegacyCoroutineScopeOnTestLevel(true)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `useLegacyTestCoroutine can't be changed from true in test to false on steps`() {
        sut.useLegacyCoroutineScopeOnTestLevel(true)
        sut.useLegacyCoroutineScopeOnStepsLevel(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `useLegacyTestCoroutine can't be changed from false in test to true on steps`() {
        sut.useLegacyCoroutineScopeOnTestLevel(false)
        sut.useLegacyCoroutineScopeOnStepsLevel(true)
    }
}