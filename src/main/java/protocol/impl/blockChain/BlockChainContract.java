package protocol.impl.blockChain;

import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.entity.ContractEntity;
import model.entity.EthereumKey;
import protocol.api.EstablisherContract ;

import java.math.BigInteger;

/**
 * Created by alex on 21/03/17.
 */
public class BlockChainContract extends EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner>{

    ContractEntity entityContract ;
    EthereumContract ethContract ;

    public BlockChainContract(ContractEntity contract) {
        entityContract = contract ;
    }

    @Override
    public void addSignature(EthereumKey k, EthereumSignature s) {
        
    }

    @Override
    public boolean isFinalized() {
        return false;
    }

    @Override
    public boolean checkContrat(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> contrat) {
        return false;
    }

    @Override
    public boolean equals(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> c) {
        return false;
    }

    @Override
    public byte[] getHashableData() {
        return new byte[0];
    }

    @Override
    public EthereumSignature sign(EthereumSigner signer, EthereumKey k) {
        return null;
    }
}
