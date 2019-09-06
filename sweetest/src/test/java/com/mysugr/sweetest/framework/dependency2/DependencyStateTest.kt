package com.mysugr.sweetest.framework.dependency2

import org.junit.Test

class DependencyStateTest {

    @Test
    fun `Initializes instance`() {
        val expected = A()
        val state = DependencyState(A::class) { Unit }
        state.instance = expected
        assert(state.getInstance() === expected)
    }

    @Test(expected = IllegalStateException::class)
    fun `Checks for incorrect type`() {
        val state = DependencyState(A::class) { Unit }
        state.checkCorrectTypeOf(B())
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't use instance of wrong type`() {
        val state = DependencyState(A::class) { Unit }
        state.instance = B()
        state.getInstanceChecked(A::class)
    }

    @Test
    fun `Can retrieve instance`() {
        val expected = A()
        val state = DependencyState(A::class) { Unit }
        state.instance = expected
        val actual = state.getInstance()
        assert(actual === expected)
    }

    @Test
    fun `Correct state of isInitialized`() {
        val state = DependencyState(A::class) { Unit }
        assert(!state.isInitialized)
        state.instance = A()
        assert(state.isInitialized)
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't retrieve instance when not initialized`() {
        val sut = DependencyState(A::class) { Unit }
        sut.getInstance()
    }

    class A

    class B
}