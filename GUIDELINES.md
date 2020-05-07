# sweetest test development guidelines

After some time working with sweetest we came up to the conclusion there is a lot of freedom as to how to design tests. But on the other side there is an increasing need for alignment. These guidelines are here for reaching an appropriate level of alignment. Feel free to challenge the current state and to contribute!

## Content

* [Goals](#goals)
* [Introduction](#introduction)
* [Reference](#reference)
* [Principles](#principles)
* [Links](#links)

## Goals

* Put a **layer of abstraction** on the system under test: we call these abstractions _steps_ (as derived from Cucumber)
  * so if the system under test changes, most parts of the test system don't need to change
  * so the test just tells _what_ is tested, not _how_ (tests become more business-centric; whenever possible, all _technical implementation_ is in the steps)
  * so test code, especially its technical implementation (steps classes), can be reused
* **Simplify dependency tree creation** by using configuration and automatic dependency resolution
* **Test setup is simplified** so **integration tests become default**
  * which **reduces the use of mocks** and
  * leads to **more realistic tests**

## Introduction

This introduction guides you through the setup of a typical sweetest test. It's goal is to be as comprehensive as possible for people not yet familiar with sweetest. Further details are outlined in the [Reference](#reference) chapter of this guidelines document.

### Add a module configuration

Given you have a module `app` you can create a file `AppModuleTestingConfiguration.kt` in the root package of your module, e.g. `com.example.app`.

```kotlin
val appModuleTestingConfiguration = moduleTestingConfiguration { ... }
```

### Add dependencies to the configuration

sweetest puts dependencies together for you by configuration. So if you have a complex system under test you don't have to create the dependencies manually. If a type is required by your test, the internal dependency management of sweetest examines the constructor of the dependency and tries to satisfy all parameters. So this is a recursive process that goes on until all dependencies are created.

sweetest treats dependencies as singletons, so if in a test system a certain type of dependency is retrieved, it will always be the same instance. So don't put types under dependency management if there is more than one instance in the system while testing.

For isolation's sake of course all dependencies are purged after each test function run.

That's how you add dependencies to your test:

```kotlin
val appModuleTestingConfiguration = moduleTestingConfiguration {
    dependency any of<LoginViewModel>()
    dependency any of<AuthManager>()
    dependency any of<BackendGateway>()
    dependency any of<SessionStore>()
}
```

Basically put all dependencies in there that you plan to be auto-created by sweetest. E.g. if `LoginViewModel` required `AuthManager` in its constructor you should add `AuthManager` to the dependency configuration, and so on...

### Create a steps class

The steps class will contain the _technical implementation_ of your test:

```kotlin
package com.example.app.view

class LoginSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration)
```

You should put that class in the same package as the class with the highest abstraction level (`LoginViewModel`), in this case `com.example.app.view``

From the package and class name you can already that this steps class is not concerned about the exact types under test, but rather evolves around the idea of testing a certain feature (in this case "logging in").

You can also see that our formerly created `appModuleTestingConfiguration` is referenced. You should always reference the configuration of the module the steps class resides in.

### Create a test class

```kotlin
class LoginTest : BaseJUnitTest(appModuleTestingConfiguration) {
    val sut by steps<LoginSteps>
}
```

As you can see also the test class references the `appModuleTestingConfiguration` as it's apparently in the same module.

By using `val sut by steps<LoginSteps>` in your test class you get access to the steps class. The variable is simply called "sut" (system under test) because it's the only steps class we reference in the test.

From that it becomes clearer that the test and steps class have totally different responsibilities:

1. The **test class** should define **WHAT** is tested (the "login" feature, test cases, ...)
2. The **steps class** should define **HOW** it is tested (technically, which classes are involved, where we put mocks, ...)

### Define test cases

So let's add a test case (see [final class](https://github.com/mysugr/sweetest/blob/example-upgrade/app/src/test/java/com/mysugr/android/testing/example/view/LoginTest.kt) ):

```kotlin
@Test
fun `Login with correct credentials is successful`() {
    sut {
        givenExistingUser(email = EXISTING_EMAIL, password = EXISTING_PASSWORD, authToken = EXISTING_AUTH_TOKEN)
        whenLoggingIn(email = EXISTING_EMAIL, password = EXISTING_PASSWORD)
        thenEmailWasCheckedAtBackend(EXISTING_EMAIL)
    }
}

companion object {
    const val EXISTING_EMAIL = "existing@test.com"
    const val EXISTING_PASSWORD = "supersecure1"
    const val EXISTING_AUTH_TOKEN = "auth_token"
}
```

`sut { ... }` is used in order to get inside the scope of the steps class and call some functions there. As you can see the test is quite expressive and could as well be read by non-technical people. That's exactly as it should be. All the technical implementation is happening in the steps class then.

**Tip:** you can write all your test cases like that in the test class first and then let the IDE create the missing functions in the respective steps class (e.g. in IntelliJ: Option + Enter, select "Create member function" and select the target class, in this case the steps class). That way you can flesh out the tests TDD-style without needing to care about the technical implementation in the steps class yet.

### Add configuration in the steps class

In order to know how we set up the test we should first quickly get a grasp of the system we want to test. Here is an outline of the example with a typical Android architecture:

```
┏━━━━━━━━━━━━━━━┓
┃ LoginActivity ┃  ⟵ Android view
┗━━━━━━━━━━━━━━━┛

━━━ ᐁ UNDER TEST ᐁ ━━━

┏━━━━━━━━━━━━━━━━┓
┃ LoginViewModel ┃  ⟵ presentation
┗━━━━━━━━━━━━━━━━┛
┏━━━━━━━━━━━━━┓ 
┃ AuthManager ┃  ⟵ domain
┗━━━━━━━━━━━━━┛
┏━━━━━━━━━━━━━━━━━┓ ┏━━━━━━━━━━━━━━━┓
┃ BackendGateway* ┃ ┃ SessionStore* ┃  ⟵ data access
┗━━━━━━━━━━━━━━━━━┛ ┗━━━━━━━━━━━━━━━┛
* = mocked
```

In order to achieve that you have to add a configuration that reflects the wanted setup in the steps file:

```kotlin
override fun configure() = super.configure()
    .requireReal<LoginViewModel>()
    .requireReal<AuthManager>()
```

This would be already enough in terms of configuration to get the dependency graph created automatically! sweetest will provide Mockito mocks automatically if the dependency isn't configured otherwise, that means that `LoginViewModel` and `AuthManager` will be instantiated and the rest of the dependencies (`BackendGateway` and `SessionStore`) will be Mockito mocks.

### Add access to the dependencies in the steps class

When we look at the test case we've written above we can see that we want to put something into the top of the system under test (the view model) and see what comes out at the bottom (backend gateway). So let's add members to the class that enable us to interact with them:

```kotlin
private val viewModel by dependency<LoginViewModel>()
private val backendGateway by dependency<BackendGateway>()
```

So now let's have a look at the functions the class needs to have. As we did the design of the tests themselves first we already know we need the following functions in the steps class:

```kotlin
fun givenExistingUser(email: String, password: String, authToken: AuthToken) = TODO()

fun whenLoggingIn(email: String, password: String) = TODO()

fun thenEmailWasCheckedAtBackend(email: String) = TODO()
```

#### Add the `given` function

Let's start with the `givenExistingUser` function. `given` is used for setting up preconditions. In the prototypical sequence of "arrange", "act", "assert", this is the first step. The naming makes that apparent.

In most cases `given` will define the behavior of the system before we really start interacting with the production system (the "acting") and thus controls the behavior of the mocks or fakes we have in our test system:

```kotlin
fun givenExistingUser(email: String, password: String, authToken: AuthToken) {
    `when`(backendGateway.checkEmail(email)).thenReturn(true)
    `when`(backendGateway.login(email, password)).thenReturn(authToken)
}
```

Obviously in this case the state of a user being existent is achieved by setting up the backend gateway so it responds with specific answers.

#### Add the `when` function

Mostly functions starting with `when` interact directly with the system, so let's wire the test to the production code:

```kotlin
fun whenLoggingIn(email: String, password: String) {
    viewModel.loginOrRegister(email, password)
}
```

#### Add the `then` function

Functions starting with `then` should solely do assertions:

```kotlin
fun thenEmailWasCheckedAtBackend(email: String) {
    verify(backendGateway).checkEmail(email)
}
```

### Current state

```kotlin
class LoginTest : BaseJUnitTest(appModuleTestingConfiguration) {

    private val sut by steps<LoginSteps>()

    @Test
    fun `Logging in checks email at backend`() {
        sut {
            givenExistingUser(email = EXISTING_EMAIL, password = EXISTING_PASSWORD, authToken = EXISTING_AUTH_TOKEN)
            whenLoggingIn(email = EXISTING_EMAIL, password = EXISTING_PASSWORD)
            thenEmailWasCheckedAtBackend(EXISTING_EMAIL)
        }
    }

    companion object {
        const val EXISTING_EMAIL = "existing@test.com"
        const val EXISTING_PASSWORD = "supersecure1"
        const val EXISTING_AUTH_TOKEN = "auth_token"
    }
}

class LoginSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    private val viewModel by dependency<LoginViewModel>()
    private val backendGateway by dependency<BackendGateway>()

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireReal<AuthManager>()

    fun givenExistingUser(email: String, password: String, authToken: AuthToken) {
        `when`(backendGateway.checkEmail(email)).thenReturn(true)
        `when`(backendGateway.login(email, password)).thenReturn(authToken)
    }

    fun whenLoggingIn(email: String, password: String) {
        viewModel.loginOrRegister(email, password)
    }

    fun thenEmailWasCheckedAtBackend(email: String) {
        verify(backendGateway).checkEmail(email)
    }
}
```

### Improving structure by reusing test code

What if we want to create a simple unit test for `AuthManager`? We can't want to use the `LoginTestSteps` because it configures sweetest to integrate with `LoginViewModel`, which is not necessary now. Also the integration test is more business-facing, where an `AuthManagerTest` might really be just focused to do very technical things instead. Also the backend gateway mock might not always cater for all scenarios we want to test. So how can we come up with a generic structure that will accomodate more sophisticated future setups?

1. We can create a steps which resembles a fake version of the backend gateway that is capable of more sophisticated tasks like having fake users and acting upon that data
3. We can create a test and steps class pair which focuses on unit-testing the `AuthManager` that uses the fake backend steps class
4. We can re-wire the previous integration test to use the same fake backend steps class

So in one word: we're going to show sweetest's strengths by reusing test code.

#### The fake backend

The concept of steps classes is that they include everything needed to add a piece to the test system. In this example that will be:

* Configuration
* A fake implementation of a production interface
* Code to set up the fake
* Code to assert calls on the fake

Therefore this is how our `BackendFakeSteps` class looks like:

```kotlin
class BackendFakeSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    // The steps class creates a fake and creates a Mockito spy from it (for the sake of being able to use `verify`)
    private val instance = spy(FakeBackendGateway())

    // Here we provide an instance of `BackendGateway` to sweetest's dependency management
    override fun configure() = super.configure()
        .offerMockRequired<BackendGateway> { instance }

    // This adds a user to the fake backend
    fun givenExistingUser(backendFakeUser: BackendFakeUser) {
        instance.users += backendFakeUser
    }

    // This verifies the expected call to the fake
    fun thenEmailWasChecked(email: String) {
        verify(instance).checkEmail(email)
    }

    // Same here
    fun thenLoginWasAttempted(email: String, password: String) {
        verify(instance).login(email, password)
    }

    // The fake is private as a steps classes' aim is to abstract the technical implementation of test code.
    private class FakeBackendGateway : BackendGateway {

        // We leave the internals open to the outside class for simplicity's sake
        val users = mutableListOf<BackendFakeUser>()

        override fun checkEmail(email: String): Boolean = users.find { it.email == email } != null

        override fun login(email: String, password: String): AuthToken {
            val user = users.find { it.email == email && it.password == password }
                ?: throw UsernameOrPasswordWrongException()
            return user.authToken
        }

        override fun register(email: String, password: String): AuthToken {
            TODO()
        }

        override fun getUserData(authToken: AuthToken): User {
            TODO()
        }
    }
}
```

Maybe you noticed that the naming `BackendFakeSteps` doesn't refer to the `BackendGateway` whose behavior it tries to mimic. This is intentional: this ties it more to the abstract concept of a "backend" than to how its access is technically implemented. So if we decide to come up with a different way of accessing the backend in the future, the steps classes' API and the tests using it can remain unchanged. Therefore also the steps classes' API doesn't reveal much about the technicalities.

If you're wondering how the `BackendFakeUser` looks like, here it is:

```kotlin
data class BackendFakeUser(
    val email: String,
    val password: String,
    val authToken: AuthToken = UUID.randomUUID().toString()
) {
    companion object {
        val USER_A = BackendFakeUser("user.a@test.com", "supersecure_a")
        val USER_B = BackendFakeUser("user.b@test.com", "supersecure_b")
    }
}
```

It also contains predefined data `USER_A` and `USER_B` so it can be reused by multiple tests without the need to define constants each time. Maybe you noticed that this class is outside the steps class; that is intentional: its use reaches beyond the steps class as it is handy for various test scenarios, no matter you decide to use the fake backend or not.

#### Create the steps class for the unit test

So let's create a steps class responsible for just unit-testing `AuthManager`:

```kotlin
class AuthManagerSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    val backend by steps<BackendFakeSteps>()

    private val instance by dependency<AuthManager>()
    private val sessionStore by dependency<SessionStore>()

    override fun configure() = super.configure()
        .requireReal<AuthManager>()
}
```

As we anticipate that the fake backend and the `SessionStore` will be needed afterwards (they are the two direct dependencies of `AuthManager` which are consumed by its constructor).

There is only one `requireReal` call (`requireReal<AuthManager>()`), which clearly indicates that this steps class is indeed about unit-testing just the `AuthManager`.

But why is `backend` just called `backend` and not `backendGateway`? And why is it public? Shouldn't a steps class be a full abstraction of what's going on under the hood or encapsulate everything needed for the test class using it? Generally yes. Usually we should avoid offering internals ([Law of Demeter](https://en.wikipedia.org/wiki/Law_of_Demeter)). Access to internals should be done through public accessors in order to avoid tying code to details which are prone to change. But anyway, the key here is "prone to change": if you group and define systems around abstract business concepts these are very unlikely to change (contrary to technical implementation). This makes the fake backend a better candidate for being used and shared among various tests. So in this case it is reasonable to call the member after the abstract concept of a "backend" _and_ offering it via the steps classes' API for direct use in test classes.

#### Create the test class

```kotlin
class AuthManagerTest2 : BaseJUnitTest(appModuleTestingConfiguration) {

    private val sut by steps<AuthManagerSteps>()

    @Test
    fun `Login as existing user, attempts login`() = sut {
        backend.givenExistingUser(USER_A)
        whenPassingCredentials(USER_A.email, USER_A.password)
        backend.thenLoginWasAttempted(USER_A.email, USER_A.password)
    }

    @Test
    fun `Login as existing user, starts session`() = sut {
        backend.givenExistingUser(USER_A)
        whenPassingCredentials(USER_A.email, USER_A.password)
        thenSessionWasStarted()
    }
}
```

As you can see, `backend` is directly used and this will allow for a lot of code reuse. So let's see how that simplifies the integration test we've created in the beginning:

#### Use the fake backend in the integration test

Let's alter the steps class for the login test so it uses the fake backend:

```kotlin
class LoginSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    val backend by steps<BackendFakeSteps>() // <-- added

    private val viewModel by dependency<LoginViewModel>()
    private val backendGateway by dependency<BackendGateway>()

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
//      .requireReal<AuthManager>() <-- instead of configuring it here, we just...
        .requireSteps<AuthManagerSteps>() // <-- ...include the steps class which does the configuration

    fun whenLoggingIn(email: String, password: String) {
        viewModel.loginOrRegister(email, password)
    }

// Not needed anymore:

//  fun givenExistingUser(email: String, password: String, authToken: AuthToken) {
//      `when`(backendGateway.checkEmail(email)).thenReturn(true)
//      `when`(backendGateway.login(email, password)).thenReturn(authToken)
//  }


//  fun thenEmailWasCheckedAtBackend(email: String) {
//      verify(backendGateway).checkEmail(email)
//  }

}
```
As you can see, all the details regarding how `AuthManager` is being tested can go to the `AuthManagerSteps`, including configuration.

On top of that we add access to `backend` and again offer it as a public member so it can be used in the test. Also we use `BackendFakeUser.USER_A` in order to not only reuse test code and configuration but also test _data_! Here's the result:

```kotlin
class LoginTest : BaseJUnitTest(appModuleTestingConfiguration) {

    private val sut by steps<LoginSteps>()

    @Test
    fun `Logging in checks email at backend`() {
        sut {
            backend.givenExistingUser(USER_A)
            whenLoggingIn(USER_A.email, USER_A.password)
            backend.thenEmailWasChecked(USER_A.email)
        }
    }

// Before:

//  @Test
//  fun `Logging in checks email at backend`() {
//      sut {
//          givenExistingUser(email = EXISTING_EMAIL, password = EXISTING_PASSWORD, authToken = EXISTING_AUTH_TOKEN)
//          whenLoggingIn(email = EXISTING_EMAIL, password = EXISTING_PASSWORD)
//          thenEmailWasCheckedAtBackend(EXISTING_EMAIL)
//      }
//  }

// Not needed anymore:

//  companion object {
//      const val EXISTING_EMAIL = "existing@test.com"
//      const val EXISTING_PASSWORD = "supersecure1"
//      const val EXISTING_AUTH_TOKEN = "auth_token"
//  }
    
}
```

### Summing up

In this journey it becomes apparent that having integration tests can be simplified a lot when there is already a steps class that tests a smaller unit (`AuthManagerSteps`): it basically requires just using (`... by steps<AuthManagerSteps>()`) or including (`requireSteps<AuthManagerSteps>`) the respective steps class and as a result instead of using a mock there is suddenly a real instance of the class (`AuthManager`) in the test system with just very low effort.

The fake backend example also shows how code reuse can work very easily. In this example we were able to reduce the test code by more or less a half.

All this of course comes with a cost: steps classes have to be set up and very well thought through and preferably be modelled around abstract business models (`BackendFakeSteps`, `LoginSteps`), but that's not always possible or even reasonable (e.g. the `AuthManagerSteps` is a direct reference to a class, which is OK as it serves as an abstraction layer in two test classes).

But experience shows that this is a matter of training and can become second nature after some time.

## Reference

### Dependencies

sweetest is tailored for systems where dependency injection is used. As you most likely have no DI during unit testing sweetest makes good for that by offering its own simple way of doing it.

In sweetest all dependencies are treated as singletons, so there will be only one instance of a certain type. So it can only be used in these cases. In all other cases you have to fall back to managing object creation on your own. The automated way should cater for the very most cases, though.

#### Dependency modes

For each dependency type there are two possibilities: either it's configured to be mock or real. Unless you tell sweetest otherwise, all dependencies are set to the "mock" mode by default.

##### Mock

If a dependency is set to the "mock" mode, a Mockito mock is created for the dependency.

* The mock is created lazily on demand
* The same instance is cached for cases some class needs the same type
* The cache is cleared after each test function run

##### Real

What does it mean if you define a type as "real"?

Instance creation is different from "mock" here: the constructor of the _real_ class will be called. In case there are parameters they will be satisfied with arguments by sweetest automatically. All the arguments will be handled as dependencies exactly the same way in a recursive manner until the dependency graph is built up.

Of course dependencies of dependencies can have different modes. E.g. the `LoginViewModel` can have mode "real", but the underlying dependencies can have mode "mock".

##### Configuring and requiring modes

You should configure dependencies mostly in steps classes (but in very rare cases you might also need to do it in test classes). You can do that by calling `requireX` in the overriden `configure` function:

```kotlin
override fun configure() = super.configure()
    .requireReal<LoginViewModel>()
```

"require" here means that that you expect the type (in this case `LoginViewModel`) in its real form, not as a mock. So if the configuration says `mockOnly of<LoginViewModel>` or in another steps or test class you declare `requireMock<LoginViewModel>` there's a conflict and sweetest will throw an exception. Expectations about dependencies need to be unanbiguous in a test system.

##### Offering instances

If the standard mock and real instance creation does not work out for you feel free to use the `offerX` class of functions:

```kotlin
offerReal { AuthManager(myBackendGateway, mySessionStore) }
```

It might make sense to satisfy the constructor of `AuthManager` this way, but in most cases you should not circumvent sweetest's dependency management. So you can use `instanceOf()` which is available within the scope of the `offerX` function:

```kotlin
offerReal { AuthManager(instanceOf(), instanceOf()) }
```

You might also use a more specific type of a certain argument:

```kotlin
offerReal { AuthManager(instanceOf<CustomBackendGateway>(), instanceOf()) }
```

That way you tell sweetest's dependency management to retrieve a dependency of type `CustomBackendGateway` rather the one directly specified in `AuthManager`'s constructor.

##### Distinguish mock and real correctly

Please make sure you distinguish mock and real correctly: when you're using the production type as it's used in your product, please consider it real. Everything else is "mock" (also "spy" or "fake" is a kind of mock).

#### Special case: abstract types and type hierarchies

When consuming or configuring dependencies sweetest tries to find a dependency declaration for the type in the module configurations:

```kotlin
// in the configuration:

dependency any of<BackendGateway>()

// e.g. in the steps class

private val instance by dependency<BackendGateway>() // <-- here

override fun configure() = super.configure()
    .requireReal<BackendGateway>() // <-- here 
```

But consuming or configuring a sub-classed dependency won't work (sweetest will complain that the type was not found in the module configuration):

```kotlin
private val instance by dependency<FakeBackendGateway>() // <-- doesn't work

override fun configure() = super.configure()
    .requireReal<FakeBackendGateway>() // <-- doesn't work 
```

In cases of abstract types (abstract classes or interfaces) or when there are different types of an inheritance hierarchy under test you have to declare the top-most level type only in the module configuration! You should never declare multiple types of the same type hierarchy because sweetest currently has troubles picking the right type currently (unfortunately the picked type can be indeterministic leading to hard to debug failing tests, but this will be fixed in one of the upcoming releases)!

##### Lazy-initialized approach

The most preferred workaround for now looks like this:

```kotlin
private val _instance by dependency<BackendGateway>()
private val instance get() = _instance as FakeBackendGateway

override fun configure() = super.configure()
    .offerMockRequired<BackendGateway> { FakeBackendGateway() }
```

It's important to have `<BackendGateway>` in `dependency` and `offerMockRequired`, because that directs sweetest to the right dependency configuration.

The solution is obviously clunky, but it preserves the lazy behavior of dependency initializations. The lazy behavior helps in situations where initialization code in production classes get in the way during the initialization of the test.

##### Immediate initialization approach

 ```kotlin
private val instance = FakeBackendGateway()

override fun configure() = super.configure()
    .offerMockRequired<BackendGateway> { instance }
```

This is easier but requires the class (in this case `FakeBackendGateway`) being compatible with it being initialized that early during creation of the steps classes.

#### Special case: spy

The approach described above is also of value if you need to create a spy on a class:

```kotlin
private val instance = spy(FakeBackendGateway())

override fun configure() = super.configure()
    .offerMockRequired<BackendGateway> { instance }
```

But it's also possible to create Mockito spies like so:

```kotlin
private val instance by dependency<AuthManager>()

override fun configure() = super.configure()
    .requireSpy<AuthManager>()
```

The downside using `requireSpy` is that you have no control over the creation of the underlying class that is spied on. So if you need that, as it's the case with the `FakeBackendGateway` above, you should stick to that approach instead.

### Module testing configuration

Whenever a new module is created in your project (or when you introduce sweetest in a module) there needs to be a configuration created for that module:

```
val appModuleTestingConfiguration = moduleTestingConfiguration { ... }
```

Where should this configuration go?

* **Name** the file exactly as the configuration val but with starting with upper-case (e.g. `AppModuleTestingConfiguration.kt`)
* and put it in the same **package** as the respective module's root package (e.g. `com.example.app`)

#### Organization by modules

Whenever you add modules which depend on each other, also the test sources will depend on each other. Therefore you might decide to also modularize test sources. All test sources (including steps classes and module testing configurations) should be put into extra modules inside the respective production code module. We suggest calling them `test` or `sweetest`:

```
/
  :app
  :a
    :sweetest <-- contains test resources for A
  :b
    :sweetest <-- contains test resources for B
      
```

If `app` depends on code in A and B, the same is true for the test sources: tests in `app` would rely on test sources in `:a:test-shared` and `:b:test-shared`. That leads to a proper separation of concerns, so for example the sources in `:a:sweetest` are responsible for offering steps classes for features implemented in `:a`.

Also the module testing configuration needs to reflect the dependencies between modules. So don't forget to list all dependent configurations of a test configuration in the argument list of `moduleTestingConfiguration`:

```
val appModuleTestingConfiguration = moduleTestingConfiguration(
    aModuleTestingConfiguration,
    bModuleTestingConfiguration, ...)
{    
    ...
}
```

Never forget to adapt the module testing configurations to changes in your module structure!

#### Adding dependencies

All dependencies need to be listed in the module testing configuration:

```kotlin
val appModuleTestingConfiguration = moduleTestingConfiguration {
    dependency any of<LoginViewModel>()
    dependency any of<AuthManager>()
    dependency any of<BackendGateway>()
    dependency any of<SessionStore>()
}
```

Whenever a dependency is used like e.g. `val instance by dependency<LoginViewModel>` it has to be added in the configuration exactly once in one module testing configuration (because the type can only be present in one module likewise).

That also means that if you decide to move a type to a different module the dependency declaration needs to move to the configuration of the respective module!

#### Deprecation

Module testing configuration in its current form is likely to be deprecated in the long run. So in this guidelines just the `any` keyword is documented. This gives the user the freedom to do the configuration of dependencies on a steps class (and eventually test class) level. If you already wrote configurations using other keywords than `any` please convert it in any case possible so the deprecation of the global dependency configuration will affect you in the least impacting way possible.

But for the sake of completness here are the other possibilities:

* `dependency mockOnly of<AuthManager>()` hard-wires sweetest to always create a mock for `AuthManager` (so an exception is thrown if somewhere `requireReal<AuthManager>` or `offerRealRequired<AuthManager> { ... }` is called)
* `dependency realOnly of<AuthManager>()` hard-wires sweetest to always create a real instance for `AuthManager` (so an exception is thrown if somewhere `requireMock<AuthManager>` or `offerMockRequired<AuthManager> { ... }` is called)
* `dependency [any | realOnly | mockOnly] initializer { ... }` provides an initializer of a certain type which is called when the type is requested somewhere in the test system

### Writing test classes

#### Scope

It is a common proctice to create a tests class per production classes, but tests in sweetest should strive for being independent of the concrete solution. That means that you should rather test blocks of features or subsystems instead of classes.

**Bad example:**

```
DeviceSelectionViewModelTest
   Has correct options
   Has correct title
   Has correct color
   When selecting device A, it's not persisted
   When selecting device A and clicking save it's persisted
   When selecting device A and then B, and then click save just B is persisted
   When not selecting anything, you can't save
```

This is the old "one test per class" approach. But we want to go beyond that...

**Good example:**

```
DeviceSelectionInfoTest
   Has correct options
   Has correct title
   Has correct color
   
DeviceSelectionTest
   When selecting device A, it's not persisted
   When selecting device A and clicking save it's persisted
   When selecting device A and then B, and then click save just B is persisted
   When not selecting anything, you can't save
```

Apparently both test classes test the same physical entity (`DeviceSelectionViewModel`), but logically a separation makes sense. Also we can observe that the tests are now concerned about blocks of functionality, not classes. By which means we achieve the wanted behavior is not of any concern of the test class anymore. The physical link should be maintained by using the same package as the production class, though!

**Summary:** Strive for organizing by logical groups of functionality instead of classes, but stay in the same package as the production code.

After having the test class you need to:

* define a "SuT" steps class
* define eventual other steps classes that abstract access to or mock/fake behavior of production classes
* flesh out the test functions (test should be compilable then); and finally
* flesh out the steps classes (the tests should be runnable and failing then); and eventually
* make the tests green (if you apply behavior-driven development)

### Creating a "SuT" steps class

The first steps class you create should act as a "master", defining the system under test (SuT) as well as the test system for it.

* Almost all calls you do from the test class should go to or via this steps class.
* The steps class should define which production classes are to be be put under test
* And how the test system for it looks like

To explain this let's create an example with a simple dependency tree:

```
LoginViewModel      <-- handles requests from/to UI, uses AuthManager
  AuthManager       <-- business logic for authentication
    SessionStore    <-- persistance logic that servces the AuthManager
    BackendGateway  <-- enabling AuthManager to communicate with backend
```

To make an integration test we could consider configuring the test like that:

```
LoginViewModel      <-- real instance
  AuthManager       <-- real instance
    SessionStore    <-- fake
    BackendGateway  <-- mock
```

To do this manually we would probably have a setup code like this:

```
val backendGateway = mock<BackendGateway>()
val sessionStore = mock<SessionStore>()
val authManager = AuthManager(backendGateway, sessionStore)
val sut = LoginViewModel(authManager)
```

But with _sweetest_ you don't have to do that, which comes in handy especially for more complex structures.

So first let's create the steps class:

```
class LoginIntegrationSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration)
```

If you want to know exactly what the view model is integrated with you just have to have a look at the configuration of the steps class. Let's add a configuration to the class:

```
override fun configure() = super.configure()
    .requireReal<LoginViewModel>()
    .requireReal<AuthManager>()

```

That means that we do exactly what is stated in the code block above, just that sweetest now wires up the dependencies for us. We'll learn later how we then work with these dependencies.

In my opinion now is the right time to go back to the test class, include the steps class and go on with fleshing out the test itself.

### Writing steps classes

#### Scope and name

There are multiple options how steps classes can be named and scoped; some examples:

1. As abstraction of a class (`LoginViewModelSteps`): use this if a steps class solely concentrates on interacting with or mocking/faking a single specific class or interface
2. As abstraction of an integration of classes (`LoginViewModelIntegrationSteps`): in cases where the class or interface is tested in integration with other classes
3. As abstraction of a feature or subsystem (`LoginSteps`): in cases where the inner structure of a feature should be further hidden from the test

It's apparent that the last option is the one that gives the system under test the highest flexibility, as changes made to the feature or subsystem optimally only force the steps class to change, not the test. In comparison, the first two options make the production code harder to change, e.g. if a class is added in between or some are merged some steps and test classes possibly need to be completely changed.

#### Regarding mocks/fakes

As already discussed, there may be two modes a dependency can be acted on by a steps class: as mock (or fake) or real instance:

1. **Mock/fake:** in this case the steps classes' job is to let it's user control the behavior of the mock or do verifications on it
2. **Real instance:** in this case the steps classes' job is to route calls from the test to the object under test

In principle both modes could be accomodated in one steps class like this:

```
// When instance is mock
fun givenNextReturnsTrue() = `when`(instance.next()).thenReturn(true)

// When instance is real
fun whenClickingNext() = instance.next()

// When instance is mock or spy
fun thenNextIsCalled() = verify(instance).next()
```

Also one can react to whether an instance is a mock or real in the setup block:

```
override fun configure() = super.configure()
    .onSetUp {
        if (MockUtil.isMock(instance)) {
            doTheStubbing()
        }
    }
```

While both modes can be accomodated in one steps class in accordance to which mode a certain dependency is configured, it should be avoided in order to prevent ambiguity and confusion. So if it's possible for a dependency to be real or mocked depending on the configuration, there should be two different steps classes for that.

And if a steps classes' job is to provide access to a mock or fake of a certain feature, subsystem or class, that should be made clear in the class name; some examples:

1. `AuthManagerMockSteps`: this steps class can be used by the `LoginSteps` class so the `AuthManager`; the test can use `AuthManagerSteps` to define the mock's behavior and do verifications
2. `BackendGatewayFakeSteps`: this steps class provides a fake version of the `BackendGateway` interface
3. `BackendFakeSteps`: this steps class does the same without caring about the structure of the production code

Same as before the last option is preferred over the others. When concepts or interfaces are abstracted instead of concrete implementations it's way easier for the system to change without breaking the tests or test classes.

## WIP:

* Create a steps class for the feature under test (e.g. `LoginSteps`) which will contain all the `given`, `when` and `then` functions as well as dependency configuration and setup code
  * The steps class should know about the setup of the test, so for example if the steps class tailored to test the integration of multiple classes, it should also be called something like `LoginIntegrationSteps` which tests a broad stack of classes (e.g. a `LoginViewModel` talking to an `AuthManager` and a `SessionStore`)
  * Should it be that a steps class really just tests _one_ class, feel free to name it exactly like that (`LoginViewModelSteps`)
  * In case the steps class' aim is purely to offer and act on a mock or fake implementation of a class, name it with a `MockSteps` or `FakeSteps` suffix (e.g. `BackendGatewayFakeSteps` or `SessionStoreMockSteps`)
* Start with the test
  * In the test class you created add reference to the steps class with the `steps` function (e.g. `val sut by steps<LoginSteps>`) and call the value `sut` (system under test)
  * Fill up the test with `@Test`-annotated test functions
    * The test function can be named after a _scenario_ (e.g. `New user trying to log in`, `Weather condition is cloudy`)
    * Only if necessary to distinguish test cases, also add more context like what's roughly expected (e.g. `New user trying to login - welcome to the app message shown`, `Existing user trying to login - welcome back message shown`)
    * Think twice if you see a test named `givenNewUser_whenLoggingIn_thenShowWelcomeToAppMessage`
      * Test code how you write it with sweetest should already contribute a lot to support a good understanding of preconditions, actions and assumptions, so maybe you can go for shorter test names
      * The more text you use in the function name the harder to read and distinguish test cases become
      * On the other hand, the less text you use the harder to get a grasp on the meaning of the test
      * To sum up, try to find a compromise to have a sufficient information value
  * For each test case, call functions that would reside in the steps class and prefix these functions with `given`, `when` or `then`
    * Functions starting with `given` should set up the environment, as these define the assumptions that are made (e.g. `givenUserExists(email = "test@test.com", password = "supersecure")` indicating that the fake backend should act as if there was that user saved in the database). For the naming choose present tense (e.g. `givenUserExists`  or `givenInternetConnected`)
    * Functions starting with `when` should perform actions, therefore they are named in progressive form (e.g. `whenGoingOffline`, `whenInitializing`, `whenLoggingInWith(email, password)`)
    * Functions starting with `then` should perform assertions (e.g. `thenIsLoggedIn()`, `thenIsBusy()` or `thenUserIsCreated(user: User)`)
    * By starting at the test to define which functions are needed in the steps you are more focused and get a faster overview of that's needed instead of getting into details too early
  * Now the test should already be human-readable for non-tech people (with just the technical implementation in the steps class missing)
  * Remember that in the test class all data structures and function calls should be domain-specific and therefore be modelled around real-world domain objects and data visible to a user, as far as possible (if the test subject is of total technical nature this doesn't apply of course)
* Now think about which dependencies (classes or interfaces) will be under test and add new steps classes
  * You can add a steps classs per class under test (e.g. `AuthManagerSteps`)
  * As stated above (just to repeat)
    * the steps classes' job is to know "how this class is to be tested"; it can also be considered as a test API over the production code
    * these will contain the `given`, `when` and `then` functions
    * and if reasonable you can split steps classes by responsibility rather than classes, as in generally it's considered a better approach to test against concepts under test, not concrete classes; but in practice this turns out to be very hard to achieve. Testing against interfaces instead of concrete classes would be a good compromise between ease of test implementation and concern separation.
    * If the steps class is purely here for faking or mocking the real behavior of a class you should name it accordingly (e.g. `AuthManagerMockSteps` or `BackendGatewayFakeSteps`)
    * To conclude, a steps class usually can be considered responsible for interacting with or mocking/faking the behavior of a class or interface under test
  * You also need to think of dependencies
    * A dependency in sweetest can be considered as an object that is needed by another or multiple other objects and usually contains logic
    * Dependencies in sweetest are considered singletons
    * You can tell sweetest which dependencies there are in the test system to let it automatically create and inject dependencies into constructors whenever certain dependencies are needed
    * So when you request a dependency of a specific type it's automatically constructed and further dependencies in the constructor are automatically initialized as well (that relieves you of the burden of putting together huge test setups when having a lot of dependencies, which is even more cumbersome when writing integration tests)
    * In essence, sweetest has an own small dependency injection framework included
  * A dependency can be either mocked or real
    * If not configured otherwise, a dependency falls back to become a mock (generated by Mockito)
    * If you want to put a class e.g. the `LoginViewModel` under test you need to tell sweetest (see below)
    * By doing so sweetest will create an instance of `LoginViewModel` and on the way examines the constructor
    * sweetest is trying to create and inject all dependencies `LoginViewModel` needs by doing the same for all of these dependencies (so e.g. if the constructor of `LoginViewModel` looks like `constructor(private val authManager: AuthManager)` then sweetest will try to create `AuthManager`, too)
    * As `AuthManager` has not previously been configured to be "real" sweetest automatically creates a Mockito mock for it
    * All instances created are saved in sweetest's dependency management, so any subsequent request of that dependency will receive the same instances (e.g. if a hypothetical `OtherViewModel` would have a dependency `AuthManager` too then it would be provided the exact same instance as for the `LoginViewModel`)
  * What's the difference between mock and fake?
    * A **fake** is an implementation that is done for test conditions, so it should basically behave like the real implementation (so e.g. a `BackendGatewayFakeSteps` would contain an actual list of registered users that gets bigger as soon as a new user is registered and is used to verify authentication attempts)
    * A **mock** doesn't need to represent the functionality of a real object but can be tailored to what's needed in test conditions
    * Both of them can be used in order to perform verifications
* How to add a dependency
  * You need to put each dependency you use in sweetest into the **module testing configuration** (see above)
    * Add a dependency by adding `dependency any of<LoginViewModel>` to the configuration (you need to do this before you introduce the dependency to any test or steps class!)
    * Consider mock vs. real
      * For example, if you are testing just the `LoginViewModel`, you set sweetest up so it creates a _real_ `LoginViewModel` only (if you don't tell sweetest it will always create a mock for that type)
      * For example, if you are testing the integration of the `LoginViewModel` with `AuthManager` and `SessionStore` then you need to tell sweetest to create real instances for all of these (`BackendGateway` will remain mocked, though)
    * Set mode constraints in configuration
      * In your test systems, if you want to have a certain type always to be **real**, then you can tell sweetest in the configuration like this: `dependency realOnly of<LoginViewModel>` (that tells that `LoginViewModel` might not be used as a dependency in a mocked form; for `LoginViewModel` that makes total sense)
      * In your test systems, if you want to have a certain type always to be a **mock**, then you can tell sweetest in the configuration like this: `dependency mockOnly of<BackendGateway>` (that leads to sweetest only creating Mockito mocks; for `BackendGateway` that makes total sense, because we will never use the real one as we don't have any internet connection to connect to some real backend)
      * If a constraint is violated (e.g. if you configure a dependency to be real only and in a test or steps class you force the same type to be a mock) an exception will be thrown
    * If you want more control over the initialization of a dependency you can tell sweetest in the configuration how to initialize it
      * Example: `dependency realOnly initializer { LoginViewModel(..., ...) }` (in the block you have to create and return the wanted instance)
      * Example: `dependency realOnly initializer { LoginViewModel(instanceOf(), somethingCustom) }` (by using `instanceOf()` you can let sweetest's dependency management do the injection of the wanted constructor parameter)
      * Example: `dependency realOnly initializer { LoginViewModelImpl(instanceOf(), instanceOf()) }` (if `LoginViewModel` is needed in the dependency graph, `LoginViewModelImpl` is automatically picked up, as sub-types are matched as well)
      * Example: `dependency realOnly initializer<LoginViewModel> { LoginViewModelImpl(instanceOf(), instanceOf()) }` (you can explicitly define the type `LoginViewModel` if you want to have more control over which type the instance is assigned to; in the example if you demand the dependency `LoginViewModelImpl` it won't be found as it's specifically tied to `LoginViewModel`)
      * Example: `dependency mockOnly initializer { FakeBackendGateway() }` (this forces `FakeBackendGateway` to be a mock and defines how this mock is initialized; so in this case it's not a Mockito mock but a custom class!)
    * Always pick the correct module testing configuration according to the module the production class or interface is located in!
* Flesh out the technical details in the steps class(es)
  * Introduce a value that holds the instance of the steps class
    * E.g. `private val instance by dependency<LoginViewModel>`
    * This makes you able to access the dependency, no matter whether it's a real instance or mock
    * Usually you should not need more than one instance handled per steps class
  * Create functions that act on the instance (if you haven't before)
    * Example: `fun whenAttemptingLogin(email: String, password:)` (that calls e.g. `instance.login(email, password)`)
    * Example: `fun thenStateIsBusy()`
    * Example: `fun thenStateIsLoggedIn()`
    * Example: `fun thenStateIsError()`
  * In most cases `given` functions initialize mock behavior (stubbing)
    * Example: `fun givenLoginSucceeds { 'when'(instance.loginOrRegister(anyString(), anyString())).thenReturn(LoginOrRegisterResult.LOGGED_IN) }`
    * Example: `fun givenLoginException { 'when'(instance.loginOrRegister(anyString(), anyString())).thenThrow(AuthManager.WrongPasswordException()) }`
  * Configure dependencies
    * You have already configured dependencies on a module level now, but you need to configure dependencies also on a steps (or in some rare cases on test) level too
    * You can start configuring in a steps or test class with writing  `override fun configure() = super.configure()` and then you can add...
    * Example: `.onSetUp { ... }` (runs the given block before any test is run)
    * Example: `.requireMock<BackendGateway>()` (tells the dependency management that this steps class is requiring or acting on a mock of the type `BackendGateway`)
    * Example: `.requireReal<LoginViewModel>()` (the same for real)
    * Example: `.offerMock<BackendGateway> { FakeBackendGateway() }` (provides an implementation for a `BackendGateway` to dependency management, so if anyone in the test system needs a mock of a `BackendGateway` the `FakeBackendGateway` implementation will be initialized)
    * Example: `.offerMockRequired<BackendGateway> { FakeBackendGateway() }` (on top on the former it also forces dependency management that `BackendGateway` can't be configured to be real)
    * Example: `.offerReal<LoginViewModel> { LoginViewModelImpl(instanceOf(), someOtherValue) }` (provides an implementation for a `LoginViewModel` to dependency management, where in this example it's demonstrated how `instanceOf()` can be used to automatically inject the correct type into the constructor as well as some other custom value)
    * Example: `.offerRealRequired<LoginViewModel> { LoginViewModelImpl(instanceOf(), someOtherValue) }` (on top on the former it also forces dependency management that `LoginViewModel` can't be configured to be mock)
    * As you can see, you can control default configuration and initialization behavior on a module basis _and_ on a steps basis
  * The easiest way to approach configuration is to just allow what's necessary on a module level and make it more specific on the test or steps level
    * E.g. if there is a `AuthManager` which can be used as a mock and a real instance as well, configure it as `any` in module configuration
    * If in a test system there is an integration setup which requires `AuthManager` to be mock and a different setup which required `AuthManager` to be a real instance, consider the following:
    * Create a steps class which is specialized on mocking `AuthManager` and call it `AuthManagerMockSteps`
      * there you also configure the `AuthManager` dependency to be a mock only by using `requireMockOnly<>`
* If you have done that we should already have a structure like this
  * If possible, the test class refers to a **user-facing feature** under test (e.g. "LoginTest", "TemperatureDisplayTest", "DataLoggingTest", ...)
  * The the test functions are named after scenarios

## Principles

Where the introduction and reference gives a broad overview of test design and how it is implemented using sweetest, this is a condensed list of principles you should thrive for:

#### General

* Don't use sweetest if it makes no sense (e.g. classes which will never be tested in an integrated fashion)

#### Steps

* Expose properties of steps classes publicly only in cases where abstract domain-specific functionality is offered that is very unlikely to change (e.g. a fake backend might be exposed as "backend" and offer functions like "givenUserExists", so the test can simply call "users.givenUserExists(...)")

## Links

* [Mockito](https://site.mockito.org/)
* [Cucumber](https://cucumber.io/)
