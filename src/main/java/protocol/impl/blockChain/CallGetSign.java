package protocol.impl.blockChain;

import static java.lang.Thread.sleep;

/**
 * Created by alex on 26/04/17.
 */
public class CallGetSign extends ContractCallImpl implements Runnable {

    private String function ;
    private boolean sign ;

    public CallGetSign(SyncBlockChain ethereum, EthereumContract contract, String function) {
        super(ethereum, contract);
        this.function = function ;
    }

    public boolean getSign() {
        return sign;
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
                    sign = (Boolean) super.getReturnContract(function);
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
