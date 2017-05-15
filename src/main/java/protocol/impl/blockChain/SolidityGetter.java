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

    private byte[] Add1;
    private byte[] Add2;
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
                    Add1 = (byte[])  super.getReturnContract("getAdd1");
                    System.out.println("\nADD1") ;
                    Add2 = (byte[]) super.getReturnContract("getAdd2");
                    System.out.println("\nADD2") ;
                    msgSender = (byte[]) super.getReturnContract("getSender");
                    System.out.println("\n") ;
                    item1 =  (String) super.getReturnContract("getItem1");
                    System.out.println("\nITEM1") ;
                    item2 =  (String) super.getReturnContract("getItem2");
                    System.out.println("\nITEM2") ;
                    clauseA =  (String) super.getReturnContract("getClauseA");
                    System.out.println("\nCLAUSE1") ;
                    clauseB =  (String) super.getReturnContract("getClauseB");
                    System.out.println("\nCLAUSE2") ;
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
        return Add1;
    }

    public byte[] getAdd2() {
        return Add2;
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
            System.out.println("\nCurrentKey1 : " + key.toString());
            if (ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(key.getPublicKey())).equals(ByteUtil.toHexString(getAdd1()))) {
                difference = false ;
            }
            else
                System.out.println("Add1 : " + ByteUtil.toHexString(getAdd1()));
            i++ ;
        }
        if(difference)
            return false ;
        else
            difference = true ;

        while(difference && i<bc.getParties().size()) {
            EthereumKey key = bc.getParties().get(i) ;
            System.out.println("\nCurrentKey2 : " + key.toString());
            if (ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(key.getPublicKey())).equals(ByteUtil.toHexString(getAdd2()))) {
                difference = false ;
            }
            else
                System.out.println("Add2 : " + ByteUtil.toHexString(getAdd2()));
            i++ ;
        }
        if(difference)
            return false ;

        if(!bc.getClauses().contains(getClauseA())) {
            System.out.println("ClauseA : " + getClauseA()) ;
            return false;
        }
        if(!bc.getClauses().contains(getClauseB())) {
            System.out.println("ClauseB : " + getClauseB()) ;
            return false;
        }
        return true ;
    }
}