package model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import crypt.api.key.AsymKey;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by alex on 18/04/17.
 */
public class EthereumKey implements AsymKey<BigInteger>, Serializable {
    //@NotNull
	@XmlElement(name="privateKey")
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    @JsonIgnore
    private BigInteger privateKey;

    //@NotNull
    @XmlElement(name="publicKey")
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
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


}
