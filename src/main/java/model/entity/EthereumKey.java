package model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import crypt.api.key.AsymKey;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigInteger;

/**
 * Created by alex on 18/04/17.
 */
public class EthereumKey implements AsymKey<BigInteger> {
    @NotNull
//	@XmlElement(name="privateKey")
//	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    @JsonIgnore
    private BigInteger privateKey;

    @NotNull
    @XmlElement(name="publicKey")
//	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigInteger publicKey;

    @NotNull
    @XmlElement(name="p")
//	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigInteger p;

    @Override
    public BigInteger getPublicKey() {
        return publicKey;
    }
    @Override
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    @Override
    public void setPublicKey(BigInteger pbk) {
        publicKey = pbk;
    }
    @Override
    public void setPrivateKey(BigInteger pk) {
        privateKey = pk;
    }

    public BigInteger getParam(String param) {
        switch(param) {
            case "p": return p;
            default: throw new RuntimeException("param " + param + " undefined");
        }
    }
}
