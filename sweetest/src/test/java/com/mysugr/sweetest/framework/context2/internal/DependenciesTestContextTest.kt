package com.mysugr.sweetest.framework.context2.internal

import com.mysugr.sweetest.framework.dependency2.DependencyState
import org.junit.Test

class DependenciesTestContextTest {

    private val sut = DependenciesTestContext()

    @Test
    fun `Basic integration test`() {
        val expected = A()
        val state = DependencyState(A::class) { expected }
        sut.states.assignDependencyStateFor(A::class, state)
        val actual = sut.retriever.getInstanceOf(A::class)
        assert(actual == expected)
    }

    class A
}