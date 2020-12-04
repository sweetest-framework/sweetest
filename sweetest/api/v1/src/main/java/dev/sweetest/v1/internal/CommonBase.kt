package dev.sweetest.v1.internal

import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext

@SweetestIntegrationsApi
abstract class CommonBase : com.mysugr.sweetest.internal.TestElement {
    internal abstract val testContext: TestContext
}
