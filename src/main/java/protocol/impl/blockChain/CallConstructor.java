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

    private byte[] part1 ;
    private byte[] part2 ;
    private String item1 ;
    private String item2 ;
    private String clause1 ;
    private String clause2 ;

    public CallConstructor(SyncBlockChain ethereum, EthereumContract contract,
                           byte[] part1, byte[] part2, String item1, String item2, String clause1, String clause2) {

        super(ethereum, contract);
        this.part1 = part1 ;
        this.part2 = part2 ;
        this.item1 = item1 ;
        this.item2 = item2 ;
        this.clause1 = clause1;
        this.clause2 = clause2;
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
                    /*System.out.println("////////////// INCOMING /////////////////");
                    super.contractBlockChainConstructor( 2, 2, 4);
                    System.out.println("////////////// CONSTRUCTED //////////////");
                    Object res = super.getReturnContract("checkRes").toString();
                    System.out.println("Calcul juste ? : " + res);
                    System.out.println("////////////// DONE     /////////////////");*/

                    System.out.println("////////////// INCOMING /////////////////");
                    System.out.println(part1 + " / " +  part2 + " / " + item1 + " / " + item2 +  " / " + clause1 + " / " + clause2);
                    super.callFunc("init", part1, part2, item1, item2, clause1, clause2);
                    System.out.println("////////////// CONSTRUCTED //////////////");
                    System.out.println("Addresse : " + super.getReturnContract("getAdd").toString());
                    System.out.println("Item : " + super.getReturnContract("getItem"));
                    System.out.println("Signature : " + super.getReturnContract("getSignature"));
                    System.out.println("Clause A : " + super.getReturnContract("getClauseA"));
                    System.out.println("Clause B : " + super.getReturnContract("getClauseB"));
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
