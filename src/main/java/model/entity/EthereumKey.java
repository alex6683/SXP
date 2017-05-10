package model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import crypt.api.key.AsymKey;
import org.bouncycastle.pqc.math.linearalgebra.BigIntUtils;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.ethereum.util.Utils;
import org.spongycastle.util.encoders.Hex;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by alex on 18/04/17.
 */

// TODO : Utiliser Hex ou byte[] plutot que BigInteger !

public class EthereumKey extends ECKey implements AsymKey<BigInteger>, Serializable {

    //@NotNull
	@XmlElement(name="privateKey")
    @JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
    @JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    @JsonIgnore
    private BigInteger privateKey;

    //@NotNull
    @XmlElement(name="publicKey")
    @JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
    @JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigInteger publicKey;

    @Override
    public BigInteger getPublicKey() {
        return publicKey;
    }
    @Override
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    @Override
    public BigInteger getParam(String p) {
        return null;
    }

    @Override
    public void setPublicKey(BigInteger pbk) {
        publicKey = pbk;
    }
    @Override
    public void setPrivateKey(BigInteger pk) {
        privateKey = pk;
    }

    public ECKey getPrivECKey() {
        return ECKey.fromPrivate(privateKey) ;
    }

    public boolean equals(EthereumKey key) {
        return publicKey.equals(key.getPublicKey())
                && privateKey.equals(key.getPrivateKey())
                && getPrivECKey().equals(key.getPrivECKey()) ;
    }

    public String toString() {return Hex.toHexString(ByteUtil.bigIntegerToBytes(publicKey)) ; }

}
