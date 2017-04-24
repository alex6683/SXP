package protocol.impl.blockChain;

import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.crypto.ECKey;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.facade.Ethereum;
import org.ethereum.util.ByteUtil;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 24/04/17.
 */
public abstract class SendTransaction {

    private Ethereum ethereum ;
    private Map<ByteArrayWrapper, TransactionReceipt> txWaiters =
            Collections.synchronizedMap(new HashMap<ByteArrayWrapper, TransactionReceipt>());

    public SendTransaction(Ethereum eth) {
        ethereum = eth ;
    }

    public TransactionReceipt sendTxAndWait(ECKey senderAddress, byte[] receiveAddress, byte[] data) throws Exception {
        BigInteger nonce = ethereum.getRepository().getNonce(senderAddress.getAddress());
        Transaction tx = new Transaction(
                ByteUtil.bigIntegerToBytes(nonce),
                ByteUtil.longToBytesNoLeadZeroes(ethereum.getGasPrice()),
                ByteUtil.longToBytesNoLeadZeroes(3_000_000),
                receiveAddress,
                ByteUtil.ZERO_BYTE_ARRAY,
                data,
                ethereum.getChainIdForNextBlock());
        tx.sign(senderAddress);

        ethereum.submitTransaction(tx);

        return waitForTx(tx.getHash());
    }

    private TransactionReceipt waitForTx(byte[] txHash) throws InterruptedException {
        ByteArrayWrapper txHashW = new ByteArrayWrapper(txHash);
        txWaiters.put(txHashW, null);
        long startBlock = ethereum.getBlockchain().getBestBlock().getNumber();

        while(true) {
            TransactionReceipt receipt = txWaiters.get(txHashW);
            if (receipt != null) {
                return receipt;
            }
            else {
                long curBlock = ethereum.getBlockchain().getBestBlock().getNumber();
                if (curBlock > startBlock + 16) {
                    throw new RuntimeException("The transaction was not included during last 16 blocks: " + txHashW.toString().substring(0,8)) ;
                }
                else {
                    System.out.println("Waiting for block with transaction 0x" + txHashW.toString().substring(0,8) +
                            " included (" + (curBlock - startBlock) + " blocks received so far) ...") ;
                }
            }
            synchronized (this) {
                wait(20000);
            }
        }
    }
}
