package protocol.impl.blockChain;

import static java.lang.Thread.sleep;

/**
 * Created by alex on 19/04/17.
 */
public class DeployContract implements Runnable {
    private EthereumContract contract ;
    private SyncBlockChain sync ;

    public DeployContract(SyncBlockChain sync, EthereumContract contract) {
        this.sync = sync ;
        this.contract = contract ;
    }

    @Override
    public void run() {
        sync.run() ;
        while(true) {
            if (!sync.getIsSyncDone()) {
                try {
                    sleep(5000) ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {

            }
        }


    }
}
