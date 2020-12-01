package dev.sweetest.api.v2.coroutine

import dev.sweetest.api.v2.Steps

suspend operator fun <T : Steps> T.invoke(run: suspend T.() -> Unit) = run(this)
