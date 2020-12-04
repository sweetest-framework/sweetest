package dev.sweetest.v1.coroutines

import dev.sweetest.v1.COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Public access to undocumented, experimental coroutines feature
 */
@Deprecated(COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE)
object SweetestCoroutineSupport {

    @Deprecated(COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE)
    val coroutineDispatcher: CoroutineDispatcher by lazy {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }
}
