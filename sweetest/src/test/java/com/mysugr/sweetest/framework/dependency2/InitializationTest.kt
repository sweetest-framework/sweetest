package com.mysugr.sweetest.framework.dependency2

import org.junit.Assert.*
import org.junit.Test

class InitializationTest {

    @Test
    fun `Initializes instance`() {
        val a = A()
        val sut = DependencyState(A::class) { a }
        val result = sut.initializeInstance()
        assert(result === a)
        assert(sut.getInstance() === a)
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't initialize with wrong type`() {
        val sut = DependencyState(A::class) { AA() }
        sut.initializeInstance()
    }

    open class A

    class AA : A()

}