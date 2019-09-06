package com.mysugr.sweetest.framework.base2

import com.mysugr.sweetest.framework.base2.internal.dependency.*
import com.mysugr.sweetest.framework.context2.TestContext
import org.junit.Before

open class JUnitTest {

    @PublishedApi
    internal val testContext = TestContext()

    inline fun <reified T : Any> dependency(initializer: DependencyInitializer<T>) =
            consumeDependencyViaDelegate(testContext, T::class)

    /*
    @PublishedApi
    internal fun <T : Any> dependencyInternal(clazz: KClass<T>): DependencyDelegate<T> {
        val state = testContext.states.getOrPut(clazz) {
            DependencyState(clazz)
        }
    }
*/

    @Before
    fun beforeInternal() {
        testContext.workflow.run()
    }
}