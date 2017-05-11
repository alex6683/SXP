package protocol.impl.blockChain;

import model.entity.EthereumKey;
import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.listener.EthereumListenerAdapter;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by methylhaine on 11/05/17.
 */
public class SoliditySigner extends ContractCallImpl implements Runnable {

    private TransactionReceipt tx ;

    @Deprecated
    public SoliditySigner(SyncBlockChain ethereum, EthereumContract contract) {
        super(ethereum, contract);
    }

    public SoliditySigner(SyncBlockChain ethereum, EthereumContract contract, EthereumKey key) {
        super(ethereum, contract, key);
    }

    public TransactionReceipt getTx() {
        return tx;
    }

    @Override
    public void run() {
        if(sync.getEthereum() == null)
            throw new NullPointerException("Ethereum not Syncing") ;
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
                    tx = super.callFunc("signature");
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
