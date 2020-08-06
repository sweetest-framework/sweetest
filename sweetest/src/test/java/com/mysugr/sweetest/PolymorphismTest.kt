package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.environment.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PolymorphismTest {

    interface Being

    class Human : Being

    interface Animal : Being

    class Cat : Animal
    class Dog : Animal

    @Test
    fun `Polymorphism scenario`() {

        val test = object : BaseJUnitTest() {

            val animal by dependency<Animal>()
            val cat by dependency<Cat>()
            val dog by dependency<Dog>()
            val human by dependency<Human>()
            val being by dependency<Being>()

            override fun configure() = super.configure()
                .provide<Cat>()
                .provide<Dog>()
                .provide<Animal> { instanceOf<Cat>() }
                .provide<Human>()
                .provide<Being> { instanceOf<Human>() }
        }

        assertEquals(test.animal, test.cat)
        assertNotEquals(test.animal, test.dog)
        assertEquals(test.being, test.human)

        test.junitBefore()
    }
}