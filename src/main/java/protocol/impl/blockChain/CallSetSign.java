package protocol.impl.blockChain;

import model.entity.EthereumKey;
import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.listener.EthereumListenerAdapter;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by alex on 26/04/17.
 */
public class CallSetSign extends ContractCallImpl implements Runnable {

    private String function ;

    private TransactionReceipt tx ;

    @Deprecated
    public CallSetSign(SyncBlockChain ethereum, EthereumContract contract, String function) {
        super(ethereum, contract);
        this.function = function ;
    }

    public CallSetSign(SyncBlockChain ethereum, EthereumContract contract, EthereumKey keys, String function) {
        super(ethereum, contract, keys);
        this.function = function ;
    }

    public TransactionReceipt getTx() {
        return tx;
    }

    @Override
    public void run() {
        sync.getEthereum().addListener(new EthereumListenerAdapter() {
            //Check for each new Block if current Transaction is included in it
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
                for (TransactionReceipt receipt : receipts) {
                    ByteArrayWrapper txHashW = new ByteArrayWrapper(receipt.getTransaction().getHash());
                    if (txWaiters.containsKey(txHashW)) {
                        txWaiters.put(txHashW, receipt);
                        synchronized (this) {
                            notifyAll();
                        }
                    }
                }
            }
        });
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
                    tx = super.callFunctNoArgs(function);
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
