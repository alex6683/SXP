package protocol.impl.blockChain;

import model.entity.EthereumKey;
import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.listener.EthereumListenerAdapter;
import org.spongycastle.util.encoders.Hex;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by alex on 19/04/17.
 */
public class DeployContract extends SendTransaction implements Runnable {
    private EthereumContract contract ;

    @Deprecated
    public DeployContract(SyncBlockChain sync, EthereumContract contract) {
        super(sync) ;
        this.contract = contract ;
    }

    public DeployContract(SyncBlockChain sync, EthereumContract contract, EthereumKey keys) {
        super(sync, keys) ;
        this.contract = contract ;
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
                    contract.compileData(contract.compileResult()) ;
                    TransactionReceipt receipt = sendTxAndWait(
                            contract.getSender(),
                            null,
                            Hex.decode(contract.getContractMetadata().bin)
                    ) ;
                    contract.setContractAdr(receipt.getTransaction().getContractAddress()) ;
                    if(contract.isCompiled())
                        System.out.println("\n\nContract Compiled ?? : " + contract.isCompiled() + "\n\n");
                    if(contract.isDeployed())
                        System.out.println("\n\nContract Deployed !! : " + Hex.toHexString(contract.getContractAdr()) + "\n\n") ;
                } catch( Exception e) { e.printStackTrace() ; }
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
