package protocol.impl;

import controller.Application;
import crypt.api.hashs.Hasher;
import crypt.factories.HasherFactory;
import crypt.impl.signatures.EthereumSignature;
import model.api.SyncManager;
import model.entity.ContractEntity;
import model.entity.EthereumKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import org.ethereum.util.ByteUtil;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import protocol.impl.blockChain.*;
import util.TestInputGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alex on 06/05/17.
 */
public class BlockChainEstablisherTest {

    public static final int N = 2;

    private ContractEntity[] contractEntity = new ContractEntity[N] ;
    private BlockChainContract bcContractA, bcContractB;

    private ArrayList<String> setEntityContract(String... entity) {
        ArrayList<String> newEntities = new ArrayList<>();
        for (String tmp : entity) {
            newEntities.add(tmp);
        }
        return newEntities;
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
        User[] users = new User[N];
        ArrayList<String> parties = new ArrayList<>() ;
        for (int i=0; i<N; i++) {
            String login  = TestInputGenerator.getRandomAlphaWord(20);
            String password = TestInputGenerator.getRandomPwd(20);

            users[i] = new User();
            users[i].setNick(login);
            Hasher hasher = HasherFactory.createDefaultHasher();
            users[i].setSalt(HasherFactory.generateSalt());
            hasher.setSalt(users[i].getSalt());
            users[i].setPasswordHash(hasher.getHash(password.getBytes()));
            users[i].setCreatedAt(new Date());
            //Attribute EthKeys
            if(i==0)
                users[i].setEthKeys(keys0);
            else
                users[i].setEthKeys(keys1);

            SyncManager<User> em = new UserSyncManagerImpl();
            em.begin();
            em.persist(users[i]);
            em.end();

            parties.add(users[i].getId()) ;
        }

        ///////////////////////////////
        //Add Entities in Contracts Entity
        for(int i=0 ; i<N ; i++){
            contractEntity[i] = new ContractEntity() ;
            contractEntity[i].setParties(parties);
            System.out.println("USERS : " + contractEntity[i].getParties().toString());

            ArrayList<String> clauses = new ArrayList<>();
            clauses.add(users[0].getId() + " troc item1 with " + users[1].getId());
            clauses.add(users[1].getId() + " troc item2 with " + users[0].getId());
            contractEntity[i].setClauses(clauses);

            contractEntity[i].setCreatedAt(new Date());
            System.out.println("DATES : " + contractEntity[i].getCreatedAt());
        }
        //End Add Entities
        ///////////////////////////////


        ArrayList<EthereumKey> partis = new ArrayList<>();
        partis.add(users[0].getEthKeys());
        partis.add(users[1].getEthKeys());


        // Map of URIS
        HashMap<EthereumKey, String> uris = new HashMap<>() ;
        String uri = Application.getInstance().getPeer().getUri();
        for (int k=0; k<N; k++){
            EthereumKey key = new EthereumKey() ;
            key.setPublicKey(users[k].getEthKeys().getPublicKey()) ;
            uris.put(key, uri);
        }

        bcContractA = new BlockChainContract(contractEntity[0], partis);
        bcContractB = new BlockChainContract(contractEntity[1], partis);


        BlockChainEstablisher bcEstablisherA = new BlockChainEstablisher(users[0], ConfigTestA.class, uris);
        BlockChainEstablisher bcEstablisherB = new BlockChainEstablisher(users[1], ConfigTestB.class, uris);

        bcEstablisherA.initialize(bcContractA, true);

        bcEstablisherA.stopSync();

        bcEstablisherB.initialize(bcContractB, false);

        bcEstablisherB.stopSync();

        bcEstablisherA.start() ;

        //Time to sendContractAddr and set it
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        bcEstablisherA.sign(bcContractB) ;

        bcEstablisherA.stopSync() ;

        for(EthereumSignature sign : bcEstablisherA.getContract().getSignatures().values())
            System.out.println("TxFinal : " + sign.toString()) ;


        bcEstablisherB.sign(bcContractA) ;

        for(EthereumSignature sign : bcEstablisherA.getContract().getSignatures().values())
            System.out.println("EstaATxFinal : " + sign.toString()) ;

        for(EthereumSignature sign : bcEstablisherA.getContract().getSignatures().values())
            System.out.println("EstaBTxFinal : " + sign.toString()) ;

        for(String sign : contractEntity[0].getSignatures().keySet())
            System.out.println("EntityATxFinal : " + sign) ;

        for(String sign : contractEntity[0].getSignatures().keySet())
            System.out.println("EntityBTxFinal : " + sign) ;

        bcEstablisherB.stopSync();

    }

}
