package protocol.impl.blockChain;

import org.ethereum.config.SystemProperties;

import static java.lang.Thread.sleep;

/**
 * Created by methylhaine on 25/04/17.
 */
public class CallSignature extends ContractCallImpl implements Runnable {

    private String signatureUser1;

    public CallSignature (SyncBlockChain ethereum, EthereumContract contract, String signatureUser1){

        super(ethereum, contract);
        this.signatureUser1 = signatureUser1;
    }

    @Override
    public void run() {

        if (!SystemProperties.getDefault().blocksLoader().equals(""))
            sync.getEthereum().getBlockLoader().loadBlocks();
        //Wait until blockchain is fully sync
        while (true) {
            if (!sync.getIsSyncDone()) {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    System.out.println("\nPeer1 signed init ? : " + getReturnContract("getU1") +"\n");
                    callFunctNoArgs("signatureUser1");
                    System.out.println("\nPeer1 signing ... \n");
                    System.out.println("\nPeer1 signed init ? : " + getReturnContract("getU1") +"\n");

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Exit when execution Successful
                sync.getEthereum().close();
                System.exit(13);
            }
        }
    }
}
