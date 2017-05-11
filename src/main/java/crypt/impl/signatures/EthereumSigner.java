package crypt.impl.signatures;

import crypt.base.AbstractSigner;
import model.entity.EthereumKey;
import org.ethereum.util.ByteUtil;
import protocol.impl.blockChain.*;

/**
 * Created by alex on 02/05/17.
 */
public class EthereumSigner extends AbstractSigner<EthereumSignature, EthereumKey> {

    private EthereumContract contract ;
    private SyncBlockChain sync ;

    private BlockChainContract bcContract ;

    public EthereumSigner(EthereumContract contract, SyncBlockChain sync) {
        this.contract = contract ;
        this.sync = sync ;
    }

    public void setBcContract(BlockChainContract bcContract) {
        this.bcContract = bcContract;
    }

    public void setSync(SyncBlockChain sync) {
        this.sync = sync;
    }

    //TODO : Utiliser getKey() pour signer la Tx ethereum

    @Override
    public EthereumKey getKey() {
        return super.key ;
    }

    @Override
    public EthereumSignature sign(byte[] message) {
        System.out.println("\n\nSYNCING\n\n") ;

        SoliditySigner signer = new SoliditySigner(sync, contract) ;
        signer.run() ;

        if(signer.getTx() == null) {
            throw new NullPointerException("Sign Tx don't exist on the BlockChain" + signer.getTx().getError()) ;
        }

        return new EthereumSignature(signer.getTx().getTransaction());
    }

    @Override
    public boolean verify(byte[] message, EthereumSignature ethereumSignature) {
        if(bcContract == null) {
            throw new NullPointerException("BlockChainContract not Set") ;
        }

        System.out.println(ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(getKey().getPublicKey())) +
                " vérifie la Tx +\n"  + ethereumSignature.toString()) ;

        ethereumSignature.getTx().verify();

        System.out.println("\n\nvérifie Signature !");

        SolidityGetterSignature call = new SolidityGetterSignature(sync, contract) ;
        call.run();
        if(!call.getIsSigned()) {
            System.out.println("\n\nNON SIGNER PAR LES DEUX PARTIES\n");
            return false;
        }

        System.out.println("\n\nvérifie Getters !");

        SolidityGetter getter = new SolidityGetter(sync, contract) ;
        getter.run();
        if(!getter.equals(bcContract)) {
            System.out.println("\n\nCONTRAT NON EQUIVALENT\n");
            return false;
        }

        return true;
    }

}
