package protocol.impl.blockchain;

import org.junit.Test;
import protocol.impl.blockChain.RopstenConfig;

/**
 * Created by methylhaine on 24/04/17.
 */
public class BlockChainRopstenConfigTest {

    @Test
    public void test() {
        // Starting the Application for testing
        /*if (Application.getInstance() == null) {
            new Application();
            Application.getInstance().runForTests(8081);
        }*/

        System.out.println("Configuration actuelle de la BlockChain :");

        RopstenConfig ropstenConfigTest = new RopstenConfig();

        /*System.out.println();
        System.out.print(ropstenConfigTest.getConfiguration());
        System.out.println();*/
    }
}
