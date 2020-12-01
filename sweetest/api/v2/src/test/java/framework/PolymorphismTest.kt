package framework

import dev.sweetest.api.v2.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class PolymorphismTest : AutoWipeTest() {

    interface Being

    class Human : Being

    interface Animal : Being

    class Cat : Animal
    class Dog : Animal

    @Test
    fun `New polymorphism scenario`() {

        val test = object : BaseTest() {

            val animal by dependency<Animal>()
            val cat by dependency<Cat>()
            val dog by dependency<Dog>()
            val human by dependency<Human>()
            val being by dependency<Being>()

            init {
                provide<Cat>()
                provide<Dog>()
                provide<Animal> { instanceOf<Cat>() }
                provide<Human>()
                provide<Being> { instanceOf<Human>() }
            }
        }

        assertEquals(test.animal, test.cat)
        assertNotEquals(test.animal, test.dog)
        assertEquals(test.being, test.human)

        test.startWorkflow()
    }

    /**
     * No matter whether we have global configuration or not, `provide` should force the algorithm
     * to use precise type matching.
     */
    @Test
    fun `Global config NO - NEW local config`() {

        val test = object : BaseTest() {
            val cat by dependency<Cat>() // matches only Cat
            val animal by dependency<Animal>() // matches only Animal

            init {
                provide<Cat>()
                provide<Animal> { object :
                    Animal {} }
            }
        }

        assertNotSame(test.cat, test.animal) // precise matching --> different types
    }
}
