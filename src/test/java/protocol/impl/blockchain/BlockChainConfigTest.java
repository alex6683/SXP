package protocol.impl.blockchain;

import controller.Application;
import org.junit.Test;
import protocol.impl.blockChain.Config;

/**
 * Created by methylhaine on 24/04/17.
 */
public class BlockChainConfigTest {

    @Test
    public void test() {
        // Starting the Application for testing
        if (Application.getInstance() == null) {
            new Application();
            Application.getInstance().runForTests(8081);
        }
    }

    Config configTest = new Config();
}
