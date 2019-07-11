package com.mysugr.sweetest.v2.framework.core.state

class TestStateStore {

    var testState: TestState = TestState()
        private set

    fun reset() {
        testState = TestState()
    }
}

interface TestStateSource