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
//  private val backendGateway by dependency<BackendGateway>() <-- removed

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

### Tests

In order to create a test class with sweetest you have to derive from `BaseJUnitTest`:

```kotlin
class LoginTest : BaseJUnitTest(appModuleTestingConfiguration)
```

You have to reference the module testing configuration (in this case `appModuleTestingConfiguration`) of the module the component under test lies in.

### Steps

Steps classes are means of **organizing and abstracting test code**. The easiest way to implement steps is to extract all technical implementation of tests into steps classes, leaving just business-facing function calls to the steps classes in the test class. This allows the test to look like this:

```kotlin
@Test
fun `Logging in checks email at backend`() {
    sut {
        givenExistingUser(USER_A)
        whenLoggingIn(USER_A.email, USER_A.password)
        thenEmailWasCheckedAtBackend(USER_A.email)
    }
} 
```

The steps class is then only concerned with implementing the functions referenced in the test.

The second aim of steps classes is to abstract not only the test code but also **configuration**. In order for the test class mostly being concerned with business-facing function calling, also the test setup should go to steps classes.

An instance of a steps class can only exist once during the whole test. So if the same type of steps class is retrieved, always the same instance is used.

Steps classes are initialized during a specific phase in the initialization of the framework and of course purged after each test function run to avoid side effects.

By the way: the name "steps class" is taken from Cucumber, a behavior-driven acceptance testing tool. Also there workflows are broken down into simple "steps" which can be called from the outside. sweetest of course takes this a little further by allowing for interdependent steps classes and dependency management, where dependencies can be consumed across steps classes, and was in fact designed with interoperability with Cucumber in mind.

#### Using steps classes

To create a steps class you have to derive from the `BaseSteps` class:

```kotlin
class LoginSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration)
```

You have to reference the module testing configuration (in this case `appModuleTestingConfiguration`) of the module the component under test lies in.

To consume a steps class you have to use the `steps` function inside a test or steps class:

```kotlin
val sut by steps<LoginSteps>()
```

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

Module testing configuration in its current form is likely to be deprecated in the long run. So the focus in this guidelines is on the `any` keyword. This gives the user the freedom to do the configuration of dependencies on a steps class (and eventually test class) level. If you already wrote configurations using other keywords than `any` please convert it in any case possible so the deprecation of the global dependency configuration will affect you in the least impacting way possible.

But for the sake of completness here are the other possibilities:

* `dependency mockOnly of<AuthManager>()` hard-wires sweetest to always create a mock for `AuthManager` (so an exception is thrown if somewhere `requireReal<AuthManager>` or `offerRealRequired<AuthManager> { ... }` is called)
* `dependency realOnly of<AuthManager>()` hard-wires sweetest to always create a real instance for `AuthManager` (so an exception is thrown if somewhere `requireMock<AuthManager>` or `offerMockRequired<AuthManager> { ... }` is called)
* `dependency [any | realOnly | mockOnly] initializer { ... }` provides an initializer of a certain type which is called when the type is requested somewhere in the test system

### Structuring test classes

It's a quite common practice to create a tests class per production class; that's fair for many cases! But tests in sweetest should strive for being independent of the concrete solution. That means that you should rather test blocks of features or subsystems instead of classes in a business-facing manner wherever possible.

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

Apparently both test classes test the same physical entity (`DeviceSelectionViewModel`), but logically a separation makes sense. Also we can observe that the tests are now concerned about chunks of functionality, not technical implementation. The classes should be placed in the same package as the implementation classes, though!

#### Summing up naming

1. Business-facing test (`LoginTest`): just name the test after the feature or business concept (`Login`) under test
2. Technology-facing test (`LoginViewModelTest`): in this case it's fair to use the specific component under test

### Structuring steps classes

The principle from the previous chapter don't only apply to the top-level acceptance and/or integration test classes, also steps classes should adhere whenever possible. This is especially true for the `BackendFakeSteps` class shown in the introduction part of this guidelines.

In the example we create this steps class `BackendFakeSteps` by whose name we can already tell it rather aims at the concept of a backend rather the concrete implementation of a `BackendGateway`. So the steps class is abstracting a backend on a very high level. This is good because by that it's API becomes as independent as possible from the concrete classes and data types. And by that we can feel fairly safe using this steps class in many places throughout our test suite without needing to fear future changes.

Also steps classes should be placed in the same package as the implementation classes.

#### Starting at the "SuT" steps class

A good way of explaining how to structure steps classes is to start with a "master" steps class.

The SuT steps class plays a special role. It aims at...

* Being the main touch point for the test
* Defining the setup of the test
* Resembling multiple other steps classes as needed

To explain this let's refresh our memory with the example from the introduction above:

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
    BackendGateway  <-- fake
```

To mirror this in a steps class let's create it step-by-step:

Most times a test puts testing clamps on the top and the bottom of a stack of classes to see whether what is put in at the top renders the correct results at the bottom and the other way round. So it's fair to have direct access to the `LoginViewModel` dependency at first:

```kotlin
private val viewModel by dependency<LoginViewModel>()
```

Next in the row comes the `AuthManager`: if we did a good job using sweetest we already have the `AuthManagerSteps` lying around, so we just include it:

```kotlin
requireSteps<AuthManagerSteps>() // <-- caution: this is added to `override fun configure()`
```

The `AuthManagerSteps` class takes care of the configuration and potentially what its dependencies (`SessionStore` and `BackendGateway`) should be.

So let's again go one step deeper to `SessionStore` and `BackendGateway`: in our test design we already have these set as fakes. But as this is already configured in the `AuthManagerSteps` this is already encapsulated there, so nothing needs to be done regarding the `LoginSteps`. The neat thing is, that the `AuthManagerTest` (unit test) uses exactly the same `AuthManagerSteps` - lots of duplicated code saved!

But let's again look at the whole picture: When we add testing clamps on the top (`LoginViewModel`) we definitely also want to do that at the bottom (`SessionStore` and `BackendGateway`) in order to control the "environment" of the test setup. So let's add these, too:

```kotlin
val backend by steps<BackendFakeSteps>()
val session by steps<SessionFakeSteps>()
```

These two guys encapsulate the abstract concepts of a backend and a session (as talked about above already).

So now let's look at the final result:

```kotlin
class LoginSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    val backend by steps<BackendFakeSteps>()
    val session by steps<SessionFakeSteps>()

    private val viewModel by dependency<LoginViewModel>()

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireSteps<AuthManagerSteps>()
    
    // here we can add functions that communicate with the production or test system...
    
    fun whenLoggingIn(email: String, password: String) {
        viewModel.loginOrRegister(email, password)
    }
}
```

#### Can there be too much abstraction?

A further improvement step could be to extract all code that interacts with the `LoginViewModel` to a `LoginViewModelSteps` class. That seems obvious, but be aware that abstraction can be taken too far, too. The more we organize steps classes around classes (`LoginViewModel`) instead of abstract concepts (`Login`) the more we also tie tests to the technical implementation.

For the `AuthManagerSteps` it makes sense because you can reuse a lot of code and configuration for the `LoginTest` and `AuthManagerTest`, but the `LoginSteps` class in this case is already designed to be in most cases the only steps class ever needed in order to test the `LoginViewModel`.

#### Summing up naming

Depending on what a steps class does the naming should show it as clear as possible:

1. Abstraction of a class (`LoginViewModelSteps`): use this if a steps class solely concentrates on interacting with or mocking/faking a single specific class or interface
2. Abstraction of an integration of classes (`LoginViewModelIntegrationSteps`): in cases where the class or interface is tested in integration with other classes
3. Abstraction of a feature (`LoginSteps`): in cases of business-facing tests
4. Abstraction of other subsystems (`BackendFakeSteps`): in cases where a subsystem is abstracted in business terms

As already discussed, 3 and 4 should be preferred as much as possible or feasible.

In most cases steps should revolve around real instances. So when there is an `AuthManagerSteps` it should be clear by convention that the `AuthManager` will be configured to be "real" (instead of mock or fake) inside the steps class. For `LoginSteps` it's similar: at least a portion of the test system is expected to be real instances. Of course underlying dependencies can still be mocks or fakes. But in general, in these cases the naming of such steps classes should be straightforward.

But for steps classes which introduce mocked or fake behavior make sure the name of the steps class reflects that! Examples:

* `BackendFakeSteps` show that the backend will be faked
* `AuthManagerMockSteps` shows that `AuthManager` will be configured as mock (which could be a viable way of unit-testing the view model, for example)
* in contrast to `AuthManagerSteps`, where `AuthManager` is configured as real

It is possible to have code for interaction with a real instance _and_ a mock (e.g. stubbing) in one steps class, but then it's hard to tell the purpose of the steps class from the name, so it's better to separate the steps classes, as you will never need them both in the same test system, too.

## Links

* [Mockito](https://site.mockito.org/)
* [Cucumber](https://cucumber.io/)
