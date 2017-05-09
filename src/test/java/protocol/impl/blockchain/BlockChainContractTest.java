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
import org.junit.Test;
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

        // Creating the users
        User[] users = new User[N];
        String[] logins = new String[N];
        String[] passwords = new String[N];
        for (int i=0; i<N; i++){
            logins[i] = createString(5);
            passwords[i] = createString(10);

            users[i] = new User();
            users[i].setNick(logins[i]);
            Hasher hasher = HasherFactory.createDefaultHasher();
            users[i].setSalt(HasherFactory.generateSalt());
            hasher.setSalt(users[i].getSalt());
            users[i].setPasswordHash(hasher.getHash(passwords[i].getBytes()));
            users[i].setCreatedAt(new Date());
            users[i].setEthKeys(new EthereumKey());
            SyncManager<User> em = new UserSyncManagerImpl();
            em.begin();
            em.persist(users[i]);
            em.end();

            Authentifier auth = Application.getInstance().getAuth();
            LoginToken token = new LoginToken();
            token.setToken(auth.getToken(logins[i], passwords[i]));
            token.setUserid(users[i].getId());

            //Add partie in Contract Entity
            contractEntity.setParties(setEntityContract(users[i].getId()));
        }

        System.out.println("" + contractEntity.getParties().toString()) ;
    }
}
