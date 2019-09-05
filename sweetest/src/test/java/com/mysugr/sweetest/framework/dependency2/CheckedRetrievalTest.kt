package com.mysugr.sweetest.framework.dependency2

import org.junit.Assert.*
import org.junit.Test

class CheckedRetrievalTest {

    @Test
    fun `Can retrieve instance`() {
        val a = A()
        val sut = DependencyState(A::class) { a }
        sut.initializeInstance()
        assert(sut.getInstanceChecked(A::class) === a)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Can'r retrieve instance with wrong requested type`() {
        val a = A()
        val sut = DependencyState(A::class) { a }
        sut.initializeInstance()
        assert(sut.getInstanceChecked(AA::class) === a)
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't retrieve instance when not initialized`() {
        val a = A()
        val sut = DependencyState(A::class) { a }
        sut.getInstanceChecked(A::class)
    }

    open class A

    class AA : A()

}