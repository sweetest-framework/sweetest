#set( $PROD_NAME = $NAME.replace("IntegrationSteps", "").replace("Steps", "").replace("Mock", "").replace("Fake", "").replace("Stub", "").replace("Test", "") )
package ${PACKAGE_NAME}

import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.TestContext

class ${NAME}(testContext: TestContext) : BaseSteps(testContext, ) {

    val steps by steps<>()
    
    private val instance by dependency<${PROD_NAME}>()
    private val steps2 by steps<>()

    override fun configure() = super.configure()
        .provide<${PROD_NAME}>()
        .onSetUp { }
    
}
