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
public class CallConstructor extends ContractCallImpl implements Runnable {

    private String part1 ;
    private String part2 ;
    private String item1 ;
    private String item2 ;

    public CallConstructor(SyncBlockChain ethereum, EthereumContract contract,
                           String part1, String part2) {

        super(ethereum, contract);
        this.part1 = part1 ;
        this.part2 = part2 ;
        // this.item1 = item1 ;
        // this.item2 = item2 ;
    }


    @Override
    public void run() {
        //sync.run() ;
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
                    System.out.println("////////////// INCOMING /////////////////");
                    super.callFunc("owned");
                    System.out.println("////////////// OWNED    /////////////////");
                    super.callFunc("transferOwnership", "49a337147d9249ffe437a780fd6ba1ffd3e2bdad");
                    System.out.println("////////////// TRANSFER /////////////////");
                    super.callFunc("initMember1");
                    System.out.println("////////////// INIT1    /////////////////");
                    super.callFunc("initMember2");
                    System.out.println("////////////// INIT2    /////////////////");
                    super.callFunc("signature1");
                    System.out.println("////////////// SIGN1    /////////////////");
                    super.callFunc("signature2");
                    System.out.println("////////////// SIGN2    /////////////////");
                    Object res = super.getReturnContract("launchTrade");
                    System.out.println("CONTRAT VALIDE : " + res);
                    System.out.println("////////////// DONE     /////////////////");
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
