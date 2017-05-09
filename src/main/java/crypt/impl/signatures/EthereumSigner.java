package crypt.impl.signatures;

import crypt.base.AbstractSigner;
import model.entity.EthereumKey;
import protocol.impl.blockChain.*;

/**
 * Created by alex on 02/05/17.
 */
public class EthereumSigner extends AbstractSigner<EthereumSignature, EthereumKey> {

    private EthereumContract contract ;
    private SyncBlockChain sync ;

    public EthereumSigner(EthereumContract contract, SyncBlockChain sync) {
        this.contract = contract ;
        this.sync = sync ;
    }

    //TODO : Utiliser getKey() pour signer la Tx ethereum

    @Override
    public EthereumKey getKey() {
        return super.key ;
    }

    @Override
    public EthereumSignature sign(byte[] message) {

        CallSetSign signer = new CallSetSign(sync, contract, getKey(), "signatureUser1") ;
        signer.run() ;

        if(signer.getTx() == null) {
            throw new NullPointerException("Sign Tx don't exist on the BlockChain" + signer.getTx().getError()) ;
        }

        return new EthereumSignature(signer.getTx().getTransaction());
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
        //VERIFIE TOUT LES GETTERS
        /*if(!call.getTODO) {
            sync.closeSync();
            return false ;
        }*/

        return true;
    }
}
