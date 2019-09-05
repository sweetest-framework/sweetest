package com.mysugr.sweetest.framework.context2.internal.dependencies

import com.mysugr.sweetest.framework.context2.internal.DependenciesTestContext
import com.mysugr.sweetest.framework.context2.internal.consumeDependency
import com.mysugr.sweetest.framework.dependency2.DependencyState
import org.junit.Test

class AssignmentTest {

    @Test
    fun `Can assign test state`() {
        val sut = DependenciesTestContext()
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
    }

    @Test
    fun `Can assign test state for superclass`() {
        val sut = DependenciesTestContext()
        val state = DependencyState(AAA::class) { AAA() }
        sut.assignDependencyStateFor(AA::class, state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Can't assign test state for subclass`() {
        val sut = DependenciesTestContext()
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(AA::class, state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Can't assign test state twice for same type`() {
        val sut = DependenciesTestContext()
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
        sut.assignDependencyStateFor(A::class, state)
    }

    @Test
    fun `Can consume state`() {
        val sut = DependenciesTestContext()
        val expected = A()
        val state = DependencyState(A::class) { expected }
        sut.assignDependencyStateFor(A::class, state)
        val actual = sut.consumeDependency(A::class)
        assert(actual === expected)
    }

    open class A

    open class AA : A()

    class AAA : AA()
}