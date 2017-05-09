package protocol.impl;

import com.google.common.primitives.Bytes;
import controller.Application;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.UserSyncManager;
import model.entity.EthereumKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
import org.ethereum.facade.Ethereum;
import org.mapdb.Atomic;
import protocol.api.Establisher;
import protocol.impl.blockChain.*;
import rest.api.Authentifier;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by alex on 21/03/17.
 */
public class BlockChainEstablisher extends Establisher<BigInteger, EthereumKey, EthereumSignature, EthereumSigner, BlockChainContract>{

    private final EstablisherService establisherService = (EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);

    private EthereumContract ethContract ;
    private SyncBlockChain sync ;

    private User currentUser ;

    public BlockChainEstablisher(String token, HashMap<EthereumKey, String> uri){

        // Matching the uris
        uris = uri;

        // Setup the signer
        Authentifier auth = Application.getInstance().getAuth();
        UserSyncManager users = new UserSyncManagerImpl();
        currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
        //System.out.println("ID : " + currentUser.getId()) ;
        //System.out.println("PubK : " + currentUser.getEthKeys().toString()) ;

    }

    public void initialize(BlockChainContract bcContract, boolean deploy) {
        initialize(bcContract);
        if(deploy && !ethContract.isDeployed()) {
            new DeployContract(sync, ethContract, super.signer.getKey()).run();
        }
    }

    /*public void setContractAdrTo(EthereumContract eth1, EthereumContract eth2) {
        if(eth1.isDeployed() && !eth2.isDeployed()) {
            eth2.setContractAdr(eth1.getContractAdr());
        }
        else if(!eth1.isDeployed() && eth2.isDeployed()) {
            eth1.setContractAdr(eth2.getContractAdr());
        }
        else
            try {
                throw new InterruptedException("Only one contract must be deployed on the BlockChain") ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }*/

    @Override
    public void initialize(BlockChainContract bcContract) {
        super.contract = bcContract ;
        ethContract = bcContract.getEthContract() ;
        sync = bcContract.getSync() ;
        super.signer = bcContract.getSigner() ;
        super.signer.setKey(currentUser.getEthKeys());
        bcContract.getSigner().setKey(currentUser.getEthKeys());
        sync.run() ;

        //RECEIV THE CONTRACT ADDRESS ON BLOCKCHAIN AND SAVE IT.
        //MAYBE HASHSOLIDITY
        establisherService.addListener(new ServiceListener() {
            @Override
            public void notify(Messages messages) {
                byte[] contractAdr = messages.getMessage("contract").getBytes() ;
                if(!ethContract.isDeployed()) {
                    ethContract.setContractAdr(contractAdr);
                }
            }
        }, "blala") ;
    }

    @Override
    public void start() {

    }
}
