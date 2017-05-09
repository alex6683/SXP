package crypt.impl.key;

import controller.tools.BigIntegerSerializer;
import crypt.base.AbstractAsymKey;
import org.bouncycastle.pqc.math.linearalgebra.BigIntUtils;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * Created by alex on 09/05/17.
 */
public class CreateEthAsymKey extends AbstractAsymKey<BigInteger> {

    private ECKey key ;

    public CreateEthAsymKey() {
        key = new ECKey();
        setPublicKey(ByteUtil.bytesToBigInteger(key.getAddress()));
        setPrivateKey(key.getPrivKey());
    }

    public String toString() {
        String pubkey, prkey, result ;
        pubkey = Hex.toHexString(ByteUtil.bigIntegerToBytes(getPublicKey()));
        prkey = Hex.toHexString(ByteUtil.bigIntegerToBytes(getPrivateKey()));
        result = pubkey + "\n" + prkey ;
        return result ;
    }

}
