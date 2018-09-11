package com.mysugr.android.testing.example.app

import com.mysugr.android.testing.example.app.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.AuthGateway
import com.mysugr.android.testing.example.net.UserGateway
import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.testing.framework.configuration.moduleTestingConfiguration

val appModuleTestingConfiguration = moduleTestingConfiguration {

    dependency any initializer { SessionStore() }
    dependency any initializer { UserGateway() }
    dependency any initializer { AuthGateway() }
    dependency any initializer { AuthManager(instanceOf(), instanceOf(), instanceOf()) }
    dependency any initializer { LoginViewModel(instanceOf()) }

}
