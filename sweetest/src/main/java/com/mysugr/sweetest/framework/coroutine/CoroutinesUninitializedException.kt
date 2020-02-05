package com.mysugr.sweetest.framework.coroutine

class CoroutinesUninitializedException : Exception(
    "sweetest's coroutines capabilities are used outside its valid timeframe! Please prevent using them before or " +
        "after the test function is run. Don't access them in constructors, class or property initializers, but " +
        "consider using `lazy { ... }` or do initialization in `.onSetUp { ... }` instead."
)