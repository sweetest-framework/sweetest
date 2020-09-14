#set( $PROD_NAME = $NAME.replace("IntegrationTest", "").replace("Test", "") )
package ${PACKAGE_NAME}

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import org.junit.Test

class ${NAME} : BaseJUnitTest() {

    val sut by steps<${PROD_NAME}Steps>()
    
    override fun configure() = super.configure()
        .onSetUp {  }

    @Test
    fun `Test`() = sut {
        
    }
}
