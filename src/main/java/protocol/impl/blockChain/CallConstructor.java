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
    private String clause1 ;
    private String clause2 ;

    public CallConstructor(SyncBlockChain ethereum, EthereumContract contract,
                           String part1, String part2, String item1, String item2, String clause1, String clause2) {

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
                    System.out.println("////////////// INCOMING /////////////////");
                    super.contractBlockChainConstructor(part1, part2, item1, item2, clause1, clause2);
                    System.out.println("////////////// CONSTRUCTED //////////////");
                    super.callFunc("owned");
                    System.out.println("////////////// OWNED    /////////////////");
                    super.callFunc("transferOwnership", part1);
                    System.out.println("////////////// TRANSFER /////////////////");
                    super.callFunc("signature");
                    System.out.println("////////////// SIGN1    /////////////////");
                    super.callFunc("signature");
                    System.out.println("////////////// SIGN2    /////////////////");
                    System.out.println("////////////// GETTERS  /////////////////");
                    Object ownerU1 = super.getReturnContract("getIsOwnerU1");
                    Object ownerU2 = super.getReturnContract("getIsOwnerU2");
                    Object addU1 = super.getReturnContract("getAddU1");
                    Object addU2 = super.getReturnContract("getAddU2");
                    Object itemU1 = super.getReturnContract("getItemU1");
                    Object itemU2 = super.getReturnContract("getItemU2");
                    Object signatureU1 = super.getReturnContract("getSignatureU1");
                    Object signatureU2 = super.getReturnContract("getSignatureU2");
                    Object clauseA = super.getReturnContract("getClauseA");
                    Object clauseB = super.getReturnContract("getClauseB");
                    System.out.println("U1 isOwner : " + ownerU1);
                    System.out.println("U2 isOwner : " + ownerU2);
                    System.out.println("U1 add : " + addU1);
                    System.out.println("U2 add : " + addU2);
                    System.out.println("U1 item : " + itemU1);
                    System.out.println("U2 item : " + itemU2);
                    System.out.println("U1 signature : " + signatureU1);
                    System.out.println("U2 signature : " + signatureU2);
                    System.out.println("clause A : " + clauseA);
                    System.out.println("clause B : " + clauseB);
                    System.out.println("////////////// GETTERS  /////////////////");
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
