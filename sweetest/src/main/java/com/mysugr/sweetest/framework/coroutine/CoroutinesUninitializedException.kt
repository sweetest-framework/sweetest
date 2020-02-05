package com.mysugr.sweetest.framework.coroutine

class CoroutinesUninitializedException : Exception(
    "sweetest's coroutines capabilities are used in a wrong place! " +
        "Please prevent using them after the test function is run."
)