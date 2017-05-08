package crypt.impl.signatures;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ethereum.core.Transaction;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by alex on 02/05/17.
 */
public class EthereumSignature {

    @XmlElement(name="Tx")
    private Transaction tx ;

    @XmlElement(name="contractAdr")
    private byte[] contractAdr ;

    /*
    @XmlElement(name="numBlock")
    private long numBlock ;
    */
    
    public EthereumSignature(@JsonProperty("hashSign") Transaction tx) {

        this.tx = tx;
        this.contractAdr = tx.getContractAddress() ;
    }

    public Transaction getTx() { return tx ; }

    public byte[] getContractAdr() {
        return contractAdr ;
    }
}
