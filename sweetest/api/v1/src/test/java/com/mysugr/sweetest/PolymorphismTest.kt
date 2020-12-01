package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.util.expectException
import com.mysugr.sweetest.util.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
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

    /**
     * This is the full legacy setup: there is a global module testing configuration and we use
     * the old `requireX...` family of local configuration.
     */
    @Test
    fun `Global config YES - OLD local config`() {

        val globalConfig = moduleTestingConfiguration {
            dependency any of<Cat>() // = global config
        }

        val test = object : BaseJUnitTest(globalConfig) {
            val cat by dependency<Cat>() // matches with Cat
            val animal by dependency<Animal>() // matches loosely with Cat

            override fun configure() = super.configure()
                .requireMock<Cat>() // = old local config
        }

        assertSame(test.cat, test.animal)
    }

    /**
     * When using no configuration users should also use `provide` instead of old config. Reason:
     * The global configuration outlines the user's intention of which type is needed and how it
     * should be provided. The old local configuration functions (`requireX...`) don't add
     * configuration, so there's nothing to base the configuration change on. `provide` in contrast
     * is indeed _creating_ configuration.
     */
    @Test
    fun `Global config NO - OLD local config`() {

        var caughtException: Exception? = null

        object : BaseJUnitTest() {
            override fun configure() = super.configure().also {
                try {
                    it.requireMock<Cat>()
                } catch (exception: Exception) {
                    caughtException = exception
                }
            }
        }.junitBefore()

        assertNotNull(caughtException)
        assertEquals(SweetestException::class, caughtException!!::class)
    }

    /**
     * No matter whether we have global configuration or not, `provide` should force the algorithm
     * to use precise type matching.
     */
    @Test
    fun `Global config YES - NEW local config 1`() {

        val globalConfig = moduleTestingConfiguration {
            dependency any of<Cat>() // = global config
        }

        val test = object : BaseJUnitTest(globalConfig) {
            val cat by dependency<Cat>() // matches only Cat
            val animal by dependency<Animal>() // matches only Animal

            override fun configure() = super.configure()
                .provide<Cat>()
                .provide<Animal> { mock() }
        }

        assert(test.cat !== test.animal) // precise matching --> different types
    }

    /**
     * Edge case: Only Animal is configured with `provide`, but not Cat --> complain, because when using `provide` all
     * variants of a specific type have to be configured with `provide` (strict type matching)
     */
    @Test
    fun `Global config YES - NEW local config 2`() {

        val globalConfig = moduleTestingConfiguration {
            dependency any of<Cat>() // = global config
        }

        val test = object : BaseJUnitTest(globalConfig) {
            val cat by dependency<Cat>() // matches only Cat
            val animal by dependency<Animal>() // matches only Animal

            override fun configure() = super.configure()
                .provide<Animal> { mock() } // should fail here already
        }

        test.junitBefore()

        test.animal

        expectException<SweetestException> {
            test.cat
        }
    }

    /**
     * Edge case: Only Animal is configured with `provide`, but not Cat --> complain, because when using `provide` all
     * variants of a specific type have to be configured with `provide` (strict type matching)
     */
    @Test
    fun `Global config YES - NEW local config 3`() {

        val globalConfig = moduleTestingConfiguration {
            dependency any of<Cat>() // = global config
        }

        val test = object : BaseJUnitTest(globalConfig) {
            val cat by dependency<Cat>() // matches only Cat
            val animal by dependency<Animal>() // matches only Animal

            override fun configure() = super.configure()
                .provide<Cat> { mock() } // should fail here already
        }

        test.junitBefore()

        test.cat

        expectException<SweetestException> {
            test.animal
        }
    }

    /**
     * Variant with global config, but the dependency is not configured in it
     */
    @Test
    fun `Global config YES - NEW local config 4`() {

        val globalConfig = moduleTestingConfiguration {}

        val test = object : BaseJUnitTest(globalConfig) {
            val cat by dependency<Cat>() // matches only Cat

            override fun configure() = super.configure()
                .provide<Cat>()
        }

        assertNotNull(test.cat)
    }

    /**
     * No matter whether we have global configuration or not, `provide` should force the algorithm
     * to use precise type matching.
     */
    @Test
    fun `Global config NO - NEW local config`() {

        val test = object : BaseJUnitTest() {
            val cat by dependency<Cat>() // matches only Cat
            val animal by dependency<Animal>() // matches only Animal

            override fun configure() = super.configure()
                .provide<Cat>()
                .provide<Animal> { mock() }
        }

        assertNotSame(test.cat, test.animal) // precise matching --> different types
    }
}
