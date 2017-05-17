package protocol.impl.blockChain;

import model.entity.EthereumKey;
import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.util.ByteUtil;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by methylhaine on 11/05/17.
 */
public class SolidityGetter extends ContractCallImpl implements Runnable {

    private byte[] add1;
    private byte[] add2;
    private byte[] msgSender;
    private String item1;
    private String item2;
    private String clauseA;
    private String clauseB;

    public SolidityGetter(SyncBlockChain ethereum, EthereumContract contract) {
        super(ethereum, contract);
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
                    System.out.println("\n\n[Getters Call] : ") ;
                    add1 = (byte[])  super.getReturnContract("getAdd1");
                    System.out.println("\t<Address A> " + ByteUtil.toHexString(add1)) ;
                    add2 = (byte[]) super.getReturnContract("getAdd2");
                    System.out.println("\t<Address B> " +ByteUtil.toHexString(add2)) ;
                    item1 =  (String) super.getReturnContract("getItem1");
                    item2 =  (String) super.getReturnContract("getItem2");
                    clauseA =  (String) super.getReturnContract("getClauseA");
                    System.out.println("\t<Clause A> " + clauseA) ;
                    clauseB =  (String) super.getReturnContract("getClauseB");
                    System.out.println("\t<Clause B> " + clauseB + "\n\n") ;
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

    public byte[] getAdd1() {
        return add1;
    }

    public byte[] getAdd2() {
        return add2;
    }

    public byte[] getMsgSender() {
        return msgSender;
    }

    public String getItem1() {
        return item1;
    }

    public String getItem2() {
        return item2;
    }

    public String getClauseA() {
        return clauseA;
    }

    public String getClauseB() {
        return clauseB;
    }

    public boolean equals(BlockChainContract bc) {
        boolean difference = true ;
        int i = 0 ;
        while(difference && i<bc.getParties().size()) {
            EthereumKey key = bc.getParties().get(i) ;
            if (ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(key.getPublicKey())).equals(ByteUtil.toHexString(getAdd1()))) {
                difference = false ;
            }
            i++ ;
        }
        if(difference)
            return false ;
        else
            difference = true ;

        while(difference && i<bc.getParties().size()) {
            EthereumKey key = bc.getParties().get(i) ;
            if (ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(key.getPublicKey())).equals(ByteUtil.toHexString(getAdd2()))) {
                difference = false ;
            }
            i++ ;
        }
        if(difference)
            return false ;

        if(!bc.getClauses().contains(getClauseA())) {
            return false;
        }
        if(!bc.getClauses().contains(getClauseB())) {
            return false;
        }
        return true ;
    }
}