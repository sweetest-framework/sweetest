package dev.sweetest.v1.internal

import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.InternalBaseTestElement

@SweetestIntegrationsApi
abstract class ApiTestElement : InternalBaseTestElement {
    internal abstract val testContext: TestContext
}
