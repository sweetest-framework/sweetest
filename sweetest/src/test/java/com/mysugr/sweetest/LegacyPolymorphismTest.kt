package com.mysugr.sweetest

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.environment.TestEnvironment
import org.junit.After
import org.junit.Test

class LegacyPolymorphismTest {

    interface Animal

    class Cat : Animal

    @After
    fun tearDown() {
        TestEnvironment.fullReset()
    }

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