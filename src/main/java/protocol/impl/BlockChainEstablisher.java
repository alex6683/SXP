package protocol.impl;

import controller.Application;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.UserSyncManager;
import model.entity.EthereumKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import network.api.EstablisherService;
import protocol.api.Establisher;
import protocol.impl.blockChain.BlockChainContract;
import protocol.impl.blockChain.EthereumContract;
import rest.api.Authentifier;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by alex on 21/03/17.
 */
public class BlockChainEstablisher extends Establisher<BigInteger, EthereumKey, EthereumSignature, EthereumSigner, BlockChainContract>{

    private final EstablisherService establisherService = (EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);

    private EthereumContract ethContract ;

    public BlockChainEstablisher(String token, HashMap<EthereumKey, String> uri){
        ethContract = new EthereumContract() ;

        // Matching the uris
        uris = uri;

        // Setup the signer
        Authentifier auth = Application.getInstance().getAuth();
        UserSyncManager users = new UserSyncManagerImpl();
        User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
        signer = new EthereumSigner(ethContract);
        signer.setKey(currentUser.getEthKeys());
    }

    @Override
    public void initialize(BlockChainContract c) {
        super.contract = c ;
    }

    @Override
    public void start() {

    }
}
