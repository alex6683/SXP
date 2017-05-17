package crypt.impl.signatures;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.util.ByteUtil;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by alex on 02/05/17.
 */
public class EthereumSignature {

    @XmlElement(name="Tx")
    private Transaction tx ;

    @XmlElement(name="contractAdr")
    private byte[] contractAdr ;

    
    public EthereumSignature(@JsonProperty("hashSign") Transaction tx) {
        this.tx = tx;
        this.contractAdr = tx.getContractAddress() ;
    }

    public EthereumSignature(@JsonProperty("hashSign") String encoded) {
        this.tx = new Transaction(ByteUtil.hexStringToBytes(encoded)) ;
        this.contractAdr = tx.getContractAddress() ;
    }

    public Transaction getTx() { return tx ; }

    public byte[] getContractAdr() {
        return contractAdr ;
    }

    public String toString() {
        return ByteUtil.toHexString(tx.getHash()) ;
    }

    public String getStringEncoded() {
        return ByteUtil.toHexString(tx.getEncoded()) ;
    }
}
