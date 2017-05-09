package protocol.impl.blockchain;

import controller.Application;
import crypt.api.hashs.Hasher;
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
import java.util.Random;

/**
 * Created by alex on 09/05/17.
 */
public class BlockChainContractTest {


    private final static Logger log = LogManager.getLogger(BlockChainContract.class);

    public static final int N = 2;

    private ContractEntity contractEntity = new ContractEntity() ;
    private BlockChainContract bcContract ;

    private ArrayList<String> setEntityContract(String... entity) {
        ArrayList<String> newEntities = new ArrayList<>() ;
        for(String tmp : entity) {
            newEntities.add(tmp) ;
        }
        return newEntities ;
    }

    private String createString(int len){
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

        if (Application.getInstance()==null){
            new Application();
            Application.getInstance().runForTests(8081);
        }

        //Add Ethereum Account
        EthereumKey keys0 = new EthereumKey() ;
        keys0.setPublicKey(ByteUtil.bytesToBigInteger(Hex.decode("0F3b00667DcA4dd54d2620f8A925aEA01FefA2d8")));
        keys0.setPrivateKey(ByteUtil.bytesToBigInteger(Hex.decode("287fc6941394e06872850966e20fe190ad43b3d0a3caa82e42cd077a6aaeb8b5")));

        EthereumKey keys1 = new EthereumKey() ;
        keys1.setPublicKey(ByteUtil.bytesToBigInteger(Hex.decode("e64CF76ECF2c4fCfDf5578ABD069eBece054465C")));
        keys1.setPrivateKey(ByteUtil.bytesToBigInteger(Hex.decode("c41bfd554363e4c8bf221dc1a1353d858c279a4cd460ec4e2f3f40866a2e416f")));


        // Creating the users
        User[] users = new User[N];
        String[] logins = new String[N];
        String[] passwords = new String[N];
        ArrayList<String> parties = new ArrayList<>() ;
        for (int i=0; i<N; i++) {
            logins[i] = createString(5);
            passwords[i] = createString(10);

            users[i] = new User();
            users[i].setNick(logins[i]);
            Hasher hasher = HasherFactory.createDefaultHasher();
            users[i].setSalt(HasherFactory.generateSalt());
            hasher.setSalt(users[i].getSalt());
            users[i].setPasswordHash(hasher.getHash(passwords[i].getBytes()));
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

            Authentifier auth = Application.getInstance().getAuth();
            LoginToken token = new LoginToken();
            token.setToken(auth.getToken(logins[i], passwords[i]));
            token.setUserid(users[i].getId());

            parties.add(users[i].getId()) ;
        }
        //Add Entity in Contract Entity
        contractEntity.setParties(parties) ;
        System.out.println("USERS : " + contractEntity.getParties().toString()) ;

        ArrayList<String> clauses = new ArrayList<>() ;
        clauses.add(users[0].getId() + " troc item1 with " + users[1].getId()) ;
        clauses.add(users[1].getId() + " troc item2 with " + users[0].getId()) ;
        contractEntity.setClauses(clauses);
        System.out.println("CLAUSES : " + contractEntity.getClauses()) ;

        contractEntity.setCreatedAt(new Date()) ;
        System.out.println("DATES : " + contractEntity.getCreatedAt()) ;

        bcContract = new BlockChainContract(contractEntity) ;

    }
}
