
package com.mysugr.android.testing.v1.example

import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration

val appModuleTestingConfiguration = moduleTestingConfiguration {

    /**
     * [SessionStore] is treated as a dependency that can't be put under unit test, but it is
     * used as a mocked version and supplied to [AuthManager]'s constructor
     */
    dependency mockOnly of<SessionStore>()

    /**
     * [BackendGateway] is treated like [SessionStore]
     */
    dependency mockOnly of<BackendGateway>()

    /**
     * AuthManager can be both a mock (when [LoginViewModel] is tested) or real (when [AuthManager]
     * itself is under test), thus the `any`. `of<Type>` lets the framework automatically analyse
     * the constructor and be supplied with the respective instances.
     */
    dependency any of<AuthManager>()

    /**
     * [LoginViewModel] can only be used as real instance in tests, as there is no other dependency
     * that uses it as a mock. Here we tell the framework explicitly how the dependency is
     * provided (or initialized).
     */
    dependency realOnly initializer { LoginViewModel(instanceOf()) }
}
