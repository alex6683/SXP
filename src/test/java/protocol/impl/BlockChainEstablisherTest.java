package protocol.impl;

import controller.Application;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.entity.ContractEntity;
import model.entity.EthereumKey;
import model.entity.LoginToken;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ethereum.util.ByteUtil;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import protocol.impl.blockChain.BlockChainContract;
import rest.api.Authentifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by alex on 06/05/17.
 */
public class BlockChainEstablisherTest {

    private final static Logger log = LogManager.getLogger(BlockChainContract.class);

    public static final int N = 2;

    private ContractEntity contractEntity = new ContractEntity();
    private BlockChainContract bcContractA, bcContractB;

    private ArrayList<String> setEntityContract(String... entity) {
        ArrayList<String> newEntities = new ArrayList<>();
        for (String tmp : entity) {
            newEntities.add(tmp);
        }
        return newEntities;
    }

    private String createString(int len) {
        // Characters we will use to encrypt
        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?".toCharArray();

        // Build a random String from the characters
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int j = 0; j < len; j++) {
            char c = characters[random.nextInt(characters.length)];
            sb.append(c);
        }
        return sb.toString();
    }


    @Test
    public void Test() {

        if (Application.getInstance() == null) {
            new Application();
            Application.getInstance().runForTests(8081);
        }

        //Add Ethereum Account
        EthereumKey keys0 = new EthereumKey();
        keys0.setPublicKey(ByteUtil.bytesToBigInteger(Hex.decode("0f3bCE1d0d5bf08310Ca3965260b6D0AE3E5b06F")));
        keys0.setPrivateKey(ByteUtil.bytesToBigInteger(Hex.decode("287fc6941394e06872850966e20fe190ad43b3d0a3caa82e42cd077a6aaeb8b5")));

        EthereumKey keys1 = new EthereumKey();
        keys1.setPublicKey(ByteUtil.bytesToBigInteger(Hex.decode("e64CF76ECF2c4fCfDf5578ABD069eBece054465C")));
        keys1.setPrivateKey(ByteUtil.bytesToBigInteger(Hex.decode("c41bfd554363e4c8bf221dc1a1353d858c279a4cd460ec4e2f3f40866a2e416f")));


        // Creating the users
        User[] u = new User[N];
        String[] logins = new String[N];
        String[] passwords = new String[N];
        for (int k=0; k<N; k++){
            logins[k] = createString(5);
            passwords[k] = createString(10);

            u[k] = new User();
            u[k].setNick(logins[k]);
            Hasher hasher = HasherFactory.createDefaultHasher();
            u[k].setSalt(HasherFactory.generateSalt());
            hasher.setSalt(u[k].getSalt());
            u[k].setPasswordHash(hasher.getHash(passwords[k].getBytes()));
            u[k].setCreatedAt(new Date());
            if(k==0)
                u[k].setEthKeys(keys0);
            else
                u[k].setEthKeys(keys1);
            SyncManager<User> em = new UserSyncManagerImpl();
            em.begin();
            em.persist(u[k]);
            em.end();

            Authentifier auth = Application.getInstance().getAuth();
            LoginToken token = new LoginToken();
            token.setToken(auth.getToken(logins[k], passwords[k]));
            token.setUserid(u[k].getId());
        }
        /*
        //Add Entity in Contract Entity
        contractEntity.setParties(parties);
        System.out.println("USERS : " + contractEntity.getParties().toString());

        ArrayList<String> clauses = new ArrayList<>();
        clauses.add(users[0].getId() + " troc item1 with " + users[1].getId());
        clauses.add(users[1].getId() + " troc item2 with " + users[0].getId());
        contractEntity.setClauses(clauses);
        System.out.println("CLAUSES : " + contractEntity.getClauses());

        contractEntity.setCreatedAt(new Date());
        System.out.println("DATES : " + contractEntity.getCreatedAt());

        ArrayList<EthereumKey> partis = new ArrayList<>();
        partis.add(users[0].getEthKeys());
        partis.add(users[1].getEthKeys());
*/
        BlockChainContract[] c = new BlockChainContract[N];

       /* bcContractA = new BlockChainContract(contractEntity, partis);
        bcContractB = new BlockChainContract(contractEntity, partis);*/

        // Creating the map of URIS
        String uri = Application.getInstance().getPeer().getUri();
        HashMap<EthereumKey, String> uris = new HashMap<>();


        for (int i = 0; i < N; i++) {
            EthereumKey key = u[i].getEthKeys();
            uris.put(key, uri);
        }


        BlockChainEstablisher[] sigmaE = new BlockChainEstablisher[N];

        for (int k = 0; k < N; k++) {
            Authentifier auth = Application.getInstance().getAuth();
            sigmaE[k] = new BlockChainEstablisher(auth.getToken(logins[k], passwords[k]), uris);
            System.out.println("TOKEN Test : " + auth.getToken(logins[k], passwords[k])) ;
            sigmaE[k].initialize(c[k]);
        }
/*

        BlockChainEstablisher bcEstablisherA ;
        BlockChainEstablisher bcEstablisherB ;

        Authentifier auth = Application.getInstance().getAuth();
        bcEstablisherA = new BlockChainEstablisher(auth.getToken(logins[0], passwords[0]), uris);
        //bcEstablisherA.initialize(bcContractA, false);

        */
/*
        auth = Application.getInstance().getAuth();
        bcEstablisherB = new BlockChainEstablisher(auth.getToken(logins[1], passwords[1]), uris);
        bcEstablisherB.initialize(bcContractB, false);
        */


    }

}
