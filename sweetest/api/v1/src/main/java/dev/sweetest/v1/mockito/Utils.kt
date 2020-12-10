package dev.sweetest.v1.mockito

import org.mockito.Mockito
import org.mockito.internal.util.MockUtil

private const val MOCKITO_DEPRECATION_MESSAGE =
    "Phased out after v1 as sweetest becomes independent of specific mocking frameworks."

@Deprecated(MOCKITO_DEPRECATION_MESSAGE)
inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

@Deprecated(MOCKITO_DEPRECATION_MESSAGE)
val Any.isMock get() = MockUtil.isMock(this)

@Deprecated(MOCKITO_DEPRECATION_MESSAGE)
val Any.isSpy get() = MockUtil.isSpy(this)
