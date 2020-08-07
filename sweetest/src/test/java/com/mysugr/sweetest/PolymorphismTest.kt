package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PolymorphismTest : BaseTest() {

    interface Being

    class Human : Being

    interface Animal : Being

    class Cat : Animal
    class Dog : Animal

    @Test
    fun `New polymorphism scenario`() {

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

    @Test(expected = Exception::class)
    fun `New polymorphism behavior (= NO module config present) - strict type lookup`() {
        object : BaseJUnitTest() {

            val animal by dependency<Animal>() // fails here

            override fun configure() = super.configure()
                .provide<Cat>()
        }
    }

    @Test
    fun `Old polymorphism behavior (= module config IS present) - loose type lookup`() {

        val moduleConfig = moduleTestingConfiguration {
            dependency any of<Animal>()
        }

        val test = object : BaseJUnitTest(moduleConfig) {

            val animal by dependency<Animal>()

            override fun configure() = super.configure()
                .provide<Cat>()
        }

        test.animal // animal can be retrieved as it is the supertype of cat
    }
}