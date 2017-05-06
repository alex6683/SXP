package protocol.impl.blockChain;

import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.crypto.ECKey;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.util.ByteUtil;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 24/04/17.
 */
public abstract class SendTransaction {

    protected SyncBlockChain sync ;
    protected Map<ByteArrayWrapper, TransactionReceipt> txWaiters =
            Collections.synchronizedMap(new HashMap<ByteArrayWrapper, TransactionReceipt>());
    protected long numBlock ;

    public SendTransaction(SyncBlockChain ethereum) {
        sync = ethereum ;
    }

    public TransactionReceipt sendTxAndWait(ECKey senderAddress, byte[] receiveAddress, byte[] data) throws Exception {
        BigInteger nonce = sync.getEthereum().getRepository().getNonce(senderAddress.getAddress());
        Transaction tx = new Transaction(
                //Nonce
                ByteUtil.bigIntegerToBytes(nonce),
                //GasPrice
                ByteUtil.longToBytesNoLeadZeroes(sync.getEthereum().getGasPrice()),
                //GasLimit
                ByteUtil.longToBytesNoLeadZeroes(3_000_000),
                receiveAddress,
                //value
                ByteUtil.ZERO_BYTE_ARRAY,
                data,
                sync.getEthereum().getChainIdForNextBlock());
        tx.sign(senderAddress);

        sync.getEthereum().submitTransaction(tx);

        return waitForTx(tx.getHash());
    }

    private TransactionReceipt waitForTx(byte[] txHash) throws InterruptedException {
        ByteArrayWrapper txHashW = new ByteArrayWrapper(txHash);
        txWaiters.put(txHashW, null);
        long startBlock = sync.getEthereum().getBlockchain().getBestBlock().getNumber();

        while(true) {
            TransactionReceipt receipt = txWaiters.get(txHashW);
            if (receipt != null) {
                return receipt;
            }
            else {
                long curBlock = sync.getEthereum().getBlockchain().getBestBlock().getNumber();
                if (curBlock > startBlock + 16) {
                    throw new RuntimeException("\n\n\nThe transaction was not included during last 16 blocks: " + txHashW.toString().substring(0,8)) ;
                }
                else {
                    System.out.println("\n\n\nWaiting for block with transaction 0x" + txHashW.toString().substring(0,8) +
                            " included (" + (curBlock - startBlock) + " blocks received so far) ...\n\n\n") ;
                }
            }
            synchronized (this) {
                wait(20000);
            }
        }
    }
}
