package protocol.impl.blockChain;

import static java.lang.Thread.sleep;

/**
 * Created by alex on 26/04/17.
 */
public class CallSetSign extends ContractCallImpl implements Runnable {

    private String function ;

    public CallSetSign(SyncBlockChain ethereum, EthereumContract contract, String function) {
        super(ethereum, contract);
        this.function = function ;
    }

    @Override
    public void run() {
        while(true) {
            if (!sync.getIsSyncDone()) {
                try {
                    sleep(5000) ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {

                try {
                    super.callFunctNoArgs(function);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    sleep(5000);
                    break ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
