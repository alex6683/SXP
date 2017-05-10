package protocol.impl.blockchain;

import org.ethereum.util.ByteUtil;
import org.junit.Test;
import protocol.impl.blockChain.*;

/**
 * Created by methylhaine on 10/05/17.
 */

public class CallConstructorTest {

    @Test
    public void test() {
        SyncBlockChain sync = new SyncBlockChain(Config.class);

        sync.run();

        EthereumContract contract = new EthereumContract();

        new DeployContract(sync, contract).run();

        new CallConstructor(sync, contract,
                ByteUtil.hexStringToBytes("e64CF76ECF2c4fCfDf5578ABD069eBece054465C" /*"0x9bee4011e4fbc6c8d4350615bff5246bd2b22da1"*/),
                ByteUtil.hexStringToBytes("0x0f3bce1d0d5bf08310ca3965260b6d0ae3e5b06f"),
                "velo",
                "carotte",
                "IZI",
                "OKLM"
        ).run();

        //Thread.currentThread().sleep(5000);
        //sync.closeSync();

        return;
    }
}
