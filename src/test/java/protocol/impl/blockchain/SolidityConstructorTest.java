package protocol.impl.blockchain;

import org.ethereum.util.ByteUtil;
import org.junit.Test;
import protocol.impl.blockChain.*;

import java.io.IOException;

/**
 * Created by methylhaine on 10/05/17.
 */

public class SolidityConstructorTest {

    @Test
    public void test() {
        SyncBlockChain sync = new SyncBlockChain(Config.class);

        sync.run();

        EthereumContract contract = null;
        try {
            contract = new EthereumContract();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new DeployContract(sync, contract).run();

        new SolidityConstructor(sync, contract,
                ByteUtil.hexStringToBytes("e64CF76ECF2c4fCfDf5578ABD069eBece054465C" /*"0x9bee4011e4fbc6c8d4350615bff5246bd2b22da1"*/),
                ByteUtil.hexStringToBytes("0x0f3bce1d0d5bf08310ca3965260b6d0ae3e5b06f"),
                "velo",
                "carotte",
                "IZI",
                "OKLM"
        ).run();

        new SoliditySigner(sync, contract).run();

        SolidityGetter solidityGetter = new SolidityGetter(sync, contract);
        solidityGetter.run();

        System.out.println("Addresse 1 : " + ByteUtil.toHexString(solidityGetter.getAdd1()));
        System.out.println("Addresse 2 : " + ByteUtil.toHexString(solidityGetter.getAdd2()));
        System.out.println("MsgSender : " + ByteUtil.toHexString(solidityGetter.getMsgSender()));
        System.out.println("Clause A : " + (solidityGetter.getClauseA()));
        System.out.println("Clause B : " + (solidityGetter.getClauseB()));
        System.out.println("Item1 : " + solidityGetter.getItem1());
        System.out.println("Item2 : " + solidityGetter.getItem2());

        SolidityGetterSignature solidityGetterSignature = new SolidityGetterSignature(sync, contract);
        solidityGetterSignature.run();

        System.out.println("Contrat signé ? : " + solidityGetterSignature.getIsSigned());


        //Thread.currentThread().sleep(5000);
        //sync.closeSync();

        return;
    }
}
