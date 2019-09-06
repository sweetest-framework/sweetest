package com.mysugr.sweetest.framework.dependency2

import org.junit.Assert.assertEquals
import org.junit.Test

class DependencyRetrieverTest {

    private val states = DependencyStateStore()
    private val sut = DependencyRetriever(states)

    @Test(expected = IllegalStateException::class)
    fun `Can't initialize with wrong type`() {
        val state = DependencyState(A::class) { B() }
        states.assignDependencyStateFor(A::class, state)
        sut.getInstanceOf(A::class)
    }

    @Test
    fun `Can retrieve instance`() {
        val expected = A()
        val state = DependencyState(A::class) { expected }
        states.assignDependencyStateFor(A::class, state)
        val actual = sut.getInstanceOf(A::class)
        assertEquals(expected, actual)
    }

    @Test fun `Nested dependency retrieval`() {
        val aState = DependencyState(A::class) { A() }
        val cState = DependencyState(C::class) { C(instanceOf()) }
        states.assignDependencyStateFor(A::class, aState)
        states.assignDependencyStateFor(C::class, cState)
        sut.getInstanceOf(A::class)
        sut.getInstanceOf(C::class)
    }

    @Test
    fun `Get or initialize instance initializes only once`() {
        var initializationCount = 0
        val state = DependencyState(A::class) {
            initializationCount++
            A()
        }
        states.assignDependencyStateFor(A::class, state)
        sut.getInstanceOf(A::class)
        sut.getInstanceOf(A::class)
        assertEquals(1, initializationCount)
    }

    class A

    class B

    class C(val a: A)
}