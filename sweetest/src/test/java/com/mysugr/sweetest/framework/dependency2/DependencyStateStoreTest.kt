package com.mysugr.sweetest.framework.dependency2

import org.junit.Test

class DependencyStateStoreTest {

    private val sut = DependencyStateStore()

    @Test
    fun `Can assign test state`() {
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
    }

    @Test
    fun `Can assign test state for superclass`() {
        val state = DependencyState(AAA::class) { AAA() }
        sut.assignDependencyStateFor(AA::class, state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Can't assign test state for subclass`() {
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(AA::class, state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Can't assign test state twice for same type`() {
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
        sut.assignDependencyStateFor(A::class, state)
    }

    @Test
    fun `Can retrieve test state`() {
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
        val actual = sut.getDependencyStateFor(A::class)
        assert(actual === state)
    }

    @Test
    fun `Can retrieve test state for various assigned types`() {
        val state = DependencyState(AA::class) { AA() }
        sut.assignDependencyStateFor(AA::class, state)
        sut.assignDependencyStateFor(A::class, state)
        val actual1 = sut.getDependencyStateFor(A::class)
        val actual2 = sut.getDependencyStateFor(AA::class)
        assert(actual1 === state)
        assert(actual2 === state)
    }

    @Test
    fun `Tests existence of state correctly`() {
        assert(!sut.hasDependencyStateFor(A::class))
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
        assert(sut.hasDependencyStateFor(A::class))
    }

    open class A

    open class AA : A()

    class AAA : AA()
}