#set( $PROD_NAME = $NAME.replace("IntegrationTest", "").replace("Test", "") )
package ${PACKAGE_NAME}

import dev.sweetest.v1.BaseJUnitTest
import dev.sweetest.v1.invoke
import dev.sweetest.v1.steps
import org.junit.Test

class ${NAME} : BaseJUnitTest() {

    val sut by steps<${PROD_NAME}Steps>()
    
    override fun configure() = super.configure()
        .onSetUp {  }

    @Test
    fun `Test`() = sut {
        
    }
}
