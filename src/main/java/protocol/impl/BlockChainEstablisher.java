package protocol.impl;

import controller.Application;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.UserSyncManager;
import model.entity.EthereumKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import protocol.api.Establisher;
import protocol.impl.blockChain.BlockChainContract;
import rest.api.Authentifier;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by alex on 10/05/17.
 */
public class BlockChainEstablisher extends Establisher<BigInteger, EthereumKey, EthereumSignature, EthereumSigner, BlockChainContract> {

    public BlockChainEstablisher(String token, HashMap<EthereumKey, String> uri) {
        // Matching the uris
        uris = uri;

        // Setup the signer
        Authentifier auth = Application.getInstance().getAuth();
        UserSyncManager users = new UserSyncManagerImpl();
        System.out.println("TOKENs : " + token) ;
        User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
        //System.out.println(currentUser.getId()) ;
    }

    @Override
    public void initialize(BlockChainContract c) {

    }

    @Override
    public void start() {

    }
}
