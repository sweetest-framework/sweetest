
package com.mysugr.android.testing.example.app

import com.mysugr.android.testing.example.app.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.testing.framework.configuration.moduleTestingConfiguration

val appModuleTestingConfiguration = moduleTestingConfiguration {

    dependency any initializer { SessionStore() }
    dependency any initializer { BackendGateway() }
    dependency any initializer { AuthManager(instanceOf(), instanceOf()) }
    dependency any initializer { LoginViewModel(instanceOf()) }

}
