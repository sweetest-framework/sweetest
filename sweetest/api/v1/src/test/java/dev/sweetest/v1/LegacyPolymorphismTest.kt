@file:Suppress("DEPRECATION")

package dev.sweetest.v1

import org.junit.Test

class LegacyPolymorphismTest : AutoWipeTest() {

    interface Animal

    class Cat : Animal
    class Dog : Animal

    @Test
    fun `Consuming supertype gives subtype`() {

        val config = moduleTestingConfiguration {
            dependency realOnly of<Cat>()
        }

        val test = object : BaseJUnitTest(config) {
            val animal by dependency<Animal>()
        }

        assert(test.animal::class == Cat::class)
    }

    @Test
    fun `Consuming supertype gives subtype (two subtypes present)`() {

        val config = moduleTestingConfiguration {
            dependency realOnly of<Cat>()
            dependency realOnly of<Dog>()
        }

        val test = object : BaseJUnitTest(config) {
            val animal by dependency<Animal>()
        }

        // loose type matching lead to the behavior that the resulting type is not really deterministic
        // therefore we introduced strict type matching (see PolymorphismTest)
        assert(test.animal::class == Dog::class || test.animal::class == Cat::class)
    }

    @Test(expected = Exception::class)
    fun `Consuming subtype doesn't give supertype`() {

        val config = moduleTestingConfiguration {
            dependency realOnly of<Animal>()
        }

        val test = object : BaseJUnitTest(config) {
            val cat by dependency<Cat>() // fails already here
        }

        test.junitBefore()

        test.cat
    }

    @Test
    fun `Locally configuring supertype configures subtype`() {

        val config = moduleTestingConfiguration {
            dependency any of<Cat>()
        }

        val test = object : BaseJUnitTest(config) {
            val animal by dependency<Animal>()

            override fun configure() = super.configure()
                .requireReal<Animal>()
        }

        assert(test.animal::class == Cat::class)
    }

    @Test(expected = Exception::class)
    fun `Locally configuring subtype doesn't configure supertype`() {

        val config = moduleTestingConfiguration {
            dependency any of<Animal>()
        }

        val test = object : BaseJUnitTest(config) {
            val cat by dependency<Cat>() // fails already here

            override fun configure() = super.configure()
                .requireReal<Cat>() // already fails here
        }

        test.junitBefore()

        test.cat
    }
}
