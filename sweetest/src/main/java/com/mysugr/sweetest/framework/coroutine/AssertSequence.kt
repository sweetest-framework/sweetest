package com.mysugr.sweetest.framework.coroutine

suspend fun assertSequence(block: suspend AssertSequence.() -> Unit) {
    block(AssertSequence())
}

class AssertSequence {
    private var currentSequenceIndex = 0
    fun expect(sequenceIndex: Int) {
        currentSequenceIndex++
        if (sequenceIndex != currentSequenceIndex) {
            throw AssertionError("Expected $currentSequenceIndex, but reached $sequenceIndex")
        }
    }
}