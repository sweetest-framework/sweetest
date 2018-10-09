# sweetest

`sweetest` is a Kotlin framework that helps you write test code for Java/Kotlin projects that can
also be **shared with [BDD](https://bit.ly/1JKQQ3h)** frameworks and does convenient **dependency
management** in test setups for you.

It facilitates test code that

1. is reusable ("don't repeat yourself")
2. readable
3. has good architecture
4. and a better [documenting function](https://bit.ly/2Ne0DaH)

Benefits:

1. Works with [mockito](https://site.mockito.org)
2. Allows sharing step definitions code with [BDD](https://bit.ly/1JKQQ3h) frameworks and syncs
   with the necessary initialization routine (tested with [Cucumber-JVM](https://bit.ly/2NhLt4s))
3. Facilitates shared code between technology-facing and business-facing automated tests, too
4. Handles dependencies conveniently for testing like in [DI](https://bit.ly/1iy5nlE) frameworks
   and creates mock versions of them automatically where needed
5. Open architecture (easy to extend and customise)

Further resources:

- [mySugr's journey to "sweetest"](http://bit.ly/sweetest-journey)
- [Introduction – create tests you actually love working with](http://bit.ly/sweetest-intro)

## In this README:

* [Customise and contribute](#customise-and-contribute)
* [How does it look?](#how-does-it-look)
* [Setup](#setup)
* [Getting started](#getting-started)
* [Guidelines](#guidelines)
* [IntelliJ live templates](#intellij-live-templates)
* [License](#license)

Further details and references to related content will be available soon!

## Customise and contribute

When you're not satisfied with the API of the framework the base interfaces can easily be customised
(these can be found in the `com.mysugr.sweetest.framework.base` package) and/or feel free to
contribute – it's highly appreciated! That way we can work on a framework and an API that's even
more usable and useful.

## How does it look?

In this example you can see that

* `AuthManager` is configured to be under test, it's dependencies are
  automatically mocked and passed to it's constructor
* Setup, mocking/stubbing, interaction with the system under test and
  assertion is abstracted away to "steps" classes

```
class AuthManagerTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireReal<AuthManager>()

    private val user by steps<UserSteps>()
    private val sut by steps<AuthManagerSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()

    @Test
    fun `Login as existing user`() {
        sut.whenLoggingInOrRegistering()
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenLoggingIn()
        }
    }

    @Test(expected = AuthManager.WrongPasswordException::class)
    fun `Login as existing user with wrong password`() {
        user.correctPassword = false
        try {
            sut.whenLoggingInOrRegistering()
        } finally {
            sessionStore.thenSessionIsNotStarted()
            backendGateway {
                thenEmailIsChecked()
                thenLoggingIn()
            }
        }
    }

    @Test
    fun `Register new user`() {
        user.exists = false
        sut.whenLoggingInOrRegistering()
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenRegistered()
        }
    }

    @Test
    fun `Logging out`() {
        sut.whenLoggingOut()
        sessionStore.thenSessionIsEnded()
    }

}
```

In order to tell the framework about all dependencies which can be put under test or can act as
mocks you have to specify them in the testing configuration:

```
val appModuleTestingConfiguration = moduleTestingConfiguration {

    /**
     * [SessionStore] is treated as a dependency that can't be put under unit test, but it is
     * used as a mocked version and supplied to [AuthManager]'s constructor
     */
    dependency mockOnly of<SessionStore>() // SessionStore works just as mock in tests

    /**
     * [BackendGateway] is treated like [SessionStore]
     */
    dependency mockOnly of<BackendGateway>() // BackendGateway works just as mock in tests

    /**
     * AuthManager can be both a mock (when [LoginViewModel] is tested) or real (when [AuthManager]
     * itself is under test), thus the `any`. `of<Type>` lets the framework automatically analyse
     * the constructor and be supplied with the respective instances.
     */
    dependency any of<AuthManager>()

    /**
     * [LoginViewModel] can only be used as real instance in tests, as there is no other dependency
     * that uses it as a mock. Here we tell the framework explicitly how the dependency is
     * initialized.
     */
    dependency realOnly initializer { LoginViewModel(instanceOf()) }

}

```

The test implementation code is abstracted into a class:

```
class AuthManagerSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure()= super.configure()
            .onSetUp(this::setUp)

    private val instance by dependency<AuthManager>()
    private val user by steps<UserSteps>()

    private fun setUp() {
        if (instance.isMock) {
            `when`(instance.loginOrRegister(anyString(), anyString())).then {
                if (user.correctPassword) {
                    if (user.exists) {
                        AuthManager.LoginOrRegisterResult.LOGGED_IN
                    } else {
                        AuthManager.LoginOrRegisterResult.REGISTERED
                    }
                } else {
                    throw AuthManager.WrongPasswordException()
                }
            }
        }
    }

    fun whenLoggingInOrRegistering() {
        instance.loginOrRegister(user.email, user.password)
    }

    fun whenLoggingOut() {
        instance.logout()
    }

    fun thenLoginOrRegisterIsCalled() {
        verify(instance).loginOrRegister(user.email, user.password)
    }

}
```

In this class you can see mocking/stubbing, interaction (`when...`) and assertion (`then...`) code.

## Setup

In your module's Gradle file please add the following line in the `dependencies` section:

```testImplementation 'com.mysugr.sweetest:sweetest:1.0.0'```

If the dependency can't be found make sure you have `jcenter()` in the `repositories` section.

## Getting started

Please follow the following steps to get started with this testing framework. Please refer to the
examples from above as they are containing important details to understand

1. For each module create a module testing configuration. Note that it's not a class but a global
   property. Create an entry for each dependency you want to put under test, including the classes
   under test themselves:<br>
   `dependency [any | mockOnly | realOnly] [of<[Type]> | initializer { [object creation] }]`
2. Create a steps class for each dependency, including the class under test (we usually name them
   `[class under test]Steps`).
3. Finally you can create the final test. Here you create references to the steps objects whose
   functions can be called.

## Guidelines

* Steps should have one private property `instance`which contains the actual instance that's
  controlled via the steps class.
* Use `given` (setup), `when` (interaction) and `then` (assertion) as prefixes for all public
  functions so the implementer can just start typing these keywords in order to narrow the
  auto-completion search down quickly.
* Use names that would read understandably in a test even for non-tech people.
* Use `import com.mysugr.sweetest.framework.base.*` to make use of the `dependency` and `steps`
  functions as well as utilities which you might need for writing convenient test and steps code.

## IntelliJ live templates

To make good for added overhead we can make use of IntelliJ's advanced refactoring tools, but on
top of that you can also use live templates to make things even easier:

<img src="readme/live-remplates-ssteps.gif">

To use them please download these [exported IntelliJ settings](tools/live-templates.jar) and import
them into your IDE at `File / Import settings` (make sure to just check live templates). After
the import you can see them in the settings at `Editor / Live Templates`:

<img src="readme/live-templates-settings.png">

You can use the templates by beginning to type the abbreviations as shown in the image above
and hitting the enter key.

## License

This project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0),
also see [license file](LICENSE).