package protocol.impl.blockChain;

import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.listener.EthereumListenerAdapter;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by alex on 25/04/17.
 */
class CallContructor extends ContractCallImpl implements Runnable {

    String part1 ;
    String part2 ;
    String item1 ;
    String item2 ;

    public CallContructor(SyncBlockChain ethereum, EthereumContract contract,
                          String part1, String part2, String item1, String item2) {
        super(ethereum, contract);
        this.item1 = item1 ;
        this.item2 = item2 ;
        this.part1 = part1 ;
        this.part2 = part2 ;
    }


    @Override
    public void run() {
        sync.run() ;
        /*sync.getEthereum().addListener(new EthereumListenerAdapter() {
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
        });*/

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

                    super.contractBlockChainConstructor(part1, part2, item1, item2);

                } catch( Exception e) { e.printStackTrace() ; }
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
}
