# sweetest changelog

## 1.2.0

* Module testing configuration is now optional and deprecated
* The use of the dependency configuration function `provide` is now mandatory if the module testing configuration is not present (this preserves backwards-compatibility _and_ forces new API)
* Strict type matching
  * before v1.2.0 it was possible to configure (e.g. `requireMock<Animal>()`) and consume (`val instance by dependency<Animal>()`) types that are subtypes of the globally configured type (`dependency any of<Cat>()`), this can be called _loose type matching_
  * this behavior lead to unpredictable behavior, therefore `provide` now enforces _strict type matching_. You will now face errors if the lack of configuration definitions leads to possible ambiguities and are instructed how to solve the problem
* There are now multiple new callbacks that can be subscribed: `onBeforeSetUp`, `onAfterSetUp`, `onTearDown`, `onAfterTearDown`
* Bugfix: the use of `HashMap` in combination with loose type matching lead to inconsistencies between locally run tests and those run on CI; this was fixed by using `LinkedHashMap` instead

## 1.1.0

* New uniform dependency configuration function `provide` for tests and steps classes
* Deprecated: `offerMock`, `offerReal`, `requireReal`, `requireMock`, `requireSpy`, `offerMockRequired`, `offerRealRequired`

## 1.0.1

* Coroutines testing support
  * `verifyOrder` tool
  * `BaseJUnitTest.testCoroutine` extension
  * `Deferred<*>.throwExceptionIfFailed` extension
* `expectException` utility
* Bugfix: use of dependency mode "spy" (e.g. when using `requireSpy`) didn't create the correct class

## 1.0.0

* Initial release