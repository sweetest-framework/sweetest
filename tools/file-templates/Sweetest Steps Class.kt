#set( $PROD_NAME = $NAME.replace("IntegrationSteps", "").replace("Steps", "").replace("Mock", "").replace("Fake", "").replace("Stub", "").replace("Test", "") )
package ${PACKAGE_NAME}

import dev.sweetest.v1.BaseSteps
import dev.sweetest.v1.dependency
import dev.sweetest.v1.steps

class ${NAME} : BaseSteps() {

    val steps by steps<>()
    
    private val instance by dependency<${PROD_NAME}>()
    private val steps2 by steps<>()

    override fun configure() = super.configure()
        .provide<${PROD_NAME}>()
        .onSetUp { }
    
}
