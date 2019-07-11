package com.mysugr.sweetest.v2.framework.component

import com.mysugr.sweetest.v2.framework.core.actor.ActorsAccessor
import com.mysugr.sweetest.v2.framework.core.stage.StagesAccessor
import com.mysugr.sweetest.v2.framework.core.state.TestStateStore
import com.mysugr.sweetest.v2.framework.core.workflow.WorkflowControllable
import com.mysugr.sweetest.v2.framework.core.workflow.WorkflowSubscribeable

open class Component : ApiComponent {

    val stateStore = TestStateStore()

    override val actors: ActorsAccessor
        get() = TODO()

    override val workflowSubscription: WorkflowSubscribeable
        get() = TODO()

    override val workflowControl: WorkflowControllable
        get() = TODO()

    override val stages: StagesAccessor
        get() = TODO()
}