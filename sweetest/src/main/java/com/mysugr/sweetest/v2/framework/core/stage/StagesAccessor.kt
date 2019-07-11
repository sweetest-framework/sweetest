package com.mysugr.sweetest.v2.framework.core.stage

import com.mysugr.sweetest.v2.framework.core.actor.IActor
import kotlin.reflect.KClass

interface StagesAccessor {
    fun registerStage(stage: IStage)
    fun <T : IStage> getStage(type: KClass<T>): T
    fun <T : IActor> getActor(type: KClass<T>): T
}