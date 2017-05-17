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

    public BlockChainContract getBcContract() {
        return bcContract;
    }

    @Override
    public EthereumKey getKey() {
        return super.key ;
    }

    @Override
    public EthereumSignature sign(byte[] message) {
        System.out.println("\n\n[Signature Processing] : " + getKey().toString() +"\n\n") ;

        SoliditySigner signer = new SoliditySigner(sync, contract, getKey()) ;
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

        System.out.println("\n\n[Verification<Transaction>] : "  + ethereumSignature.toString() + "\n\n") ;

        ethereumSignature.getTx().verify();

        System.out.println("\n\n[Verification<SignatureOnBlockChain>]\n\n");

        SolidityGetterSignature call = new SolidityGetterSignature(sync, contract) ;
        call.run();
        if(!call.getIsSigned()) {
            return false;
        }


        System.out.println("\n\n[Verification<GettersOnBlockChain>]\n\n");

        SolidityGetter getter = new SolidityGetter(sync, contract) ;
        getter.run();


        if(!getter.equals(bcContract)) {
            return false;
        }

        return true;
    }

}
