package com.mysugr.sweetest.framework.dependency2

import org.junit.Test

class RetrievalTest {

    @Test
    fun `Can retrieve instance`() {
        val a = A()
        val sut = DependencyState(A::class) { a }
        sut.initializeInstance()
        assert(sut.getInstance() === a)
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't retrieve instance when not initialized`() {
        val a = A()
        val sut = DependencyState(A::class) { a }
        sut.getInstance()
    }

    @Test
    fun `Can get or initialize instance`() {
        val expected = A()
        val sut = DependencyState(A::class) { expected }
        val actual = sut.getOrInitializeInstance(A::class)
        assert(expected === actual)
    }

    @Test
    fun `Get or initialize instance initializes only once`() {
        var initializationCount = 0
        val sut = DependencyState(A::class) {
            initializationCount++
            A()
        }
        sut.getOrInitializeInstance(A::class)
        sut.getOrInitializeInstance(A::class)
        assert(initializationCount == 1)
    }

    @Test
    fun `Correct state of isInitialized`() {
        val sut = DependencyState(A::class) { A() }
        assert(!sut.isInitialized)
        sut.getOrInitializeInstance(A::class)
        assert(sut.isInitialized)
    }

    open class A
}