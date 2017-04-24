package protocol.impl.blockChain;

import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;

import java.math.BigInteger;

/**
 * Created by alex on 24/04/17.
 */
public abstract class SendTransaction {
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
}
