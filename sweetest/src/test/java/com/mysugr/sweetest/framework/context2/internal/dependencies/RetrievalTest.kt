package com.mysugr.sweetest.framework.context2.internal.dependencies

import com.mysugr.sweetest.framework.context2.internal.DependenciesTestContext
import com.mysugr.sweetest.framework.dependency2.DependencyState
import org.junit.Test

class RetrievalTest {

    @Test
    fun `Can retrieve test state`() {
        val sut = DependenciesTestContext()
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
        val actual = sut.getDependencyStateFor(A::class)
        assert(actual === state)
    }

    @Test
    fun `Can retrieve test state for various assigned types`() {
        val sut = DependenciesTestContext()
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
        val sut = DependenciesTestContext()
        assert(!sut.hasDependencyStateFor(A::class))
        val state = DependencyState(A::class) { A() }
        sut.assignDependencyStateFor(A::class, state)
        assert(sut.hasDependencyStateFor(A::class))
    }

    open class A

    open class AA : A()
}