package com.mysugr.sweetest.v2.framework.component

import com.mysugr.sweetest.v2.framework.core.actor.ActorsAccessor
import com.mysugr.sweetest.v2.framework.core.stage.StagesAccessor
import com.mysugr.sweetest.v2.framework.core.workflow.WorkflowControllable
import com.mysugr.sweetest.v2.framework.core.workflow.WorkflowSubscribeable

interface ApiComponent {
    val actors: ActorsAccessor
    val workflowSubscription: WorkflowSubscribeable
    val workflowControl: WorkflowControllable
    val stages: StagesAccessor
}