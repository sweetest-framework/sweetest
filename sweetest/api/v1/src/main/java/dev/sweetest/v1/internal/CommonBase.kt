package dev.sweetest.v1.internal

import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.TestElement

@SweetestIntegrationsApi
abstract class CommonBase : TestElement {
    internal abstract val testContext: TestContext
}
