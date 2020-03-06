# sweetest test development guidelines

After some time working with sweetest we came up to the conclusion that it leaves a lot of freedom to design tests as we want. But on the other side there is an increasing need for alignment. These guidelines are here for reaching an appropriate level of alignment. Feel free to challenge the current state and to contribute!

## Content

* [Goals](#goals)
* [One-by-one steps](#one-by-one-steps)
* [Principles](#principles)
* [Links](#links)

## Goals

* Put a **layer of abstraction** on the system under test: we call these abstractions _steps_ (as derived from Cucumber)
  * so if the system under test changes, the test system doesn't need to change
  * so the test just tells _what_ is tested, not _how_ (tests become more business-centric, whenever possible, all the details are in the steps)
  * so you can reuse test code
  * to sum up, there is a proper separation of concerns (separation of business rules, workflow and technical implementation) and the ability to reuse test code
* **Simplify dependency tree creation** by using configuration and automatic dependency resolution
* That **simplifies test setup** which makes it **cheaper to have bigger integration tests**
* With that in mind it becomes worth to **invest more in integration tests** instead of unit tests
  * which **reduces the use of mocks** and
  * leads to **more realistic tests**

## One-by-one steps

* Create a module testing configuration in the test sources for each module in your project (e.g. for a module `app` create a file `AppModuleTestingConfiguration` with the following code: `val appModuleTestingConfiguration = moduleTestingConfiguration { ... }`; see below for more info on how to add dependencies to the configuration)
  * Always use the same package structure for test sources, e.g. if you app resides in a module `app` and has a base package `org.myorg.product.app` then put the module test configuration in exactly in the same package
  * If there is a class `org.myorg.product.app.view.LoginViewModel` then the test class `LoginViewModelTest` and `LoginViewModelSteps` would reside in the exact same package, too
  * If you have a dedicated module for test sources you should still stick to the same package structure, e.g. if the module is `:app:test` _don't_ add `test` to the package structure for test sources
  * If modules depend on each other the module testing configuration should do the same, e.g. `val appModuleTestingConfiguration = moduleTestingConfiguration(dependentConfig1, dependentConfig2, ...) { ... }`, this automatically imports all dependent configurations
* Create a test class for the _feature under test_ (e.g. `LoginTest`), so the test is agnostic of the underlying structures (classes, models, views, data structures, ...)
  * Should it be that a test really just tests _one_ class, feel free to name it exactly like that (`LoginViewModelTest`)
* Create a steps class for the feature under test (e.g. `LoginSteps`) which will contain all the `given`, `when` and `then` functions as well as dependency configuration and setup code
  * The steps class should know about the setup of the test, so for example if the steps class tailored to test the integration of multiple classes, it should also be called something like `LoginIntegrationSteps` which tests a broad stack of classes (e.g. a `LoginViewModel` talking to an `AuthManager` and a `SessionStore`)
  * Should it be that a steps class really just tests _one_ class, feel free to name it exactly like that (`LoginViewModelSteps`)
  * In case the steps class' aim is purely to offer and act on a fake implementation of a class, name it with a `FakeSteps` suffix (e.g. `BackendGatewayFakeSteps` or `SessionStoreFakeSteps`)
* Start with the test
  * In the test class you created add reference to the steps class with the `steps` function (e.g. `val sut by steps<LoginSteps>`) and call the value `sut` (system under test)
  * Fill up the test with `@Test`-annotated test functions
    * The test function can be named after a _scenario_ (e.g. `New user trying to log in`, `Weather condition is cloudy`)
    * Only if necessary to distinguish test cases, also add more context like what's roughly expected (e.g. `New user trying to login - welcome to the app message shown`, `Existing user trying to login - welcome back message shown`)
    * Don't use names like `givenNewUser_whenLoggingIn_thenShowWelcomeToAppMessage` because
      * The test code in the test functions should already be easy enough to understand preconditions, actions and assumptions
      * The more text you use the harder to distinguish test cases
      * The less spaces you use the harder to read
      * So please just use the test function names to make a meaningful distinctions to properly navigate between and understand test scenarios and cases
  * For each test case, call functions that would reside in the steps class and prefix these functions with `given`, `when` or `then`
    * Functions starting with `given` should set up the environment, as these define the assumptions that are made (e.g. `givenUser(email = "test@test.com", password = "supersecure")` indicating that the fake backend should act as if there was that user saved in the database). For the naming choose present tense (e.g. `givenUser` as a short form of "there is a user" or `givenInternetConnected`)
    * Functions starting with `when` should perform actions, therefore they are named in progressive form (e.g. `whenGoingOffline`, `whenInitializing`, `whenLoggingInWith(email, password)`)
    * Functions starting with `then` should perform assertions (e.g. `thenIsLoggedIn()`, `thenIsBusy()` or `thenUserIsCreated(user: User)`)
    * By starting at the test to define which functions are needed in the steps you are more focused and get a faster overview of that's needed instead of getting into details too early
  * Now the test should already be human-readable for non-tech people (with just the technical implementation in the steps class missing)
  * Remember that in the test class all data structures and function calls should be domain-specific and therefore be modelled around real-world domain objects and data visible to a user, as far as possible (if the test subject is of total technical nature this doesn't apply of course)
* Now think about which dependencies (classes or interfaces) will be under test and add new steps classes
  * You can add a steps classs per class under test (e.g. `AuthManagerSteps`)
  * As stated above (just to repeat)
    * the steps classes' job is to know "how this class is to be tested"
    * these will contain the `given`, `when` and `then` functions
    * and if reasonable you can split steps classes by responsibility rather than classes, as in generally it's considered a better approach to test against concepts under test, not concrete classes; but in practice this turns out to be very hard to achieve. Testing against interfaces instead of concrete classes would be a good compromise between ease of test implementation and concern separation.
    * If the steps class is purely here for faking or mocking the real behavior of a class you should name it accordingly (e.g. `AuthManagerMockSteps` or `BackendGatewayFakeSteps`)
    * To conclude, a steps class usually can be considered responsible for inetracting with or mocking/faking the behavior of a class or interface under test
  * You also need to think of dependencies
    * A dependency in sweetest can be considered as an object that is needed by another or multiple other objects and usually contains logic
    * Dependencies in sweetest are considered singletons
    * You can tell sweetest which dependencies there are in the test system to let it automatically create and inject dependencies into constructors whenever certain dependencies are needed
    * So when you request a dependency of a specific type it's automatically constructed and further dependencies in the constructor are automatically initialized as well (that relieves you of the burden of putting together huge test setups when having a lot of dependencies, which is even more cumbersome when writing integration tests)
    * In essence, sweetest has an own small dependency injection framework included
  * A dependency can be either mocked or real
    * By default in sweetest all dependencies are considered mocks
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

## Links

* [Mockito](https://site.mockito.org/)
* [Cucumber](https://cucumber.io/)
