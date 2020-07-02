# sweetest changelog

## 1.1.0

* New uniform dependency configuration function `provide` for tests and steps classes
* Deprecated: offerMock, offerReal, requireReal, requireMock, requireSpy, offerMockRequired, offerRealRequired

## 1.0.1

* Coroutines testing support
  * `verifyOrder` tool
  * `BaseJUnitTest.testCoroutine` extension
  * `Deferred<*>.throwExceptionIfFailed` extension
* `expectException` utility
* Bugfix: use of dependency mode "spy" (e.g. when using `requireSpy`) didn't create the correct class

## 1.0.0

* Initial release