
package com.mysugr.android.testing.example.app

import com.mysugr.android.testing.example.app.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.net.DummyBackendGateway
import com.mysugr.android.testing.example.state.DummySessionStore
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration

val appModuleTestingConfiguration = moduleTestingConfiguration {

    dependency any initializer { DummySessionStore() }
    dependency any initializer { DummyBackendGateway() as BackendGateway }
    dependency any initializer { AuthManager(instanceOf(), instanceOf()) }
    dependency any initializer { LoginViewModel(instanceOf()) }

}
