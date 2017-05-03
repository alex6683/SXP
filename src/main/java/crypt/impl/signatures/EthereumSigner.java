package crypt.impl.signatures;

import crypt.base.AbstractSigner;
import model.entity.EthereumKey;
import protocol.impl.blockChain.*;

/**
 * Created by alex on 02/05/17.
 */
public class EthereumSigner extends AbstractSigner<EthereumSignature, EthereumKey> {

    EthereumContract contract ;

    public EthereumSigner(EthereumContract contract) { this.contract = contract ; }

    @Override
    public EthereumKey getKey() {
        return this.key ;
    }

    @Override
    public EthereumSignature sign(byte[] message) {
        SyncBlockChain sync = new SyncBlockChain(Config.class) ;
        sync.run() ;

        CallSetSign signer = new CallSetSign(sync, contract, "signatureUser1") ;
        signer.run() ;

        sync.closeSync();

        return new EthereumSignature(signer.getTx().getTransaction(), contract.getContractAdr());
    }

    @Override
    public boolean verify(byte[] message, EthereumSignature ethereumSignature) {
        SyncBlockChain sync = new SyncBlockChain(Config.class) ;
        sync.run() ;
        ethereumSignature.getTx().verify();
        CallGetSign call = new CallGetSign(sync, contract, "getU1") ;
        call.run();
        if(!call.getSign()) {
            sync.closeSync();
            return false ;
        }
        sync.closeSync();
        return true;
    }
}
