package protocol.impl;

import controller.Application;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.entity.ContractEntity;
import model.entity.LoginToken;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import org.junit.Test;
import protocol.impl.blockChain.BlockChainContract;
import rest.api.Authentifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by alex on 06/05/17.
 */
public class BlockChainEstablisherTest {

    private ContractEntity contractEntity ;
    public static final int N = 2;

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
    public void test() {

       /* // Starting the Application to be able to test it
        if (Application.getInstance()==null){
            new Application();
            Application.getInstance().runForTests(8081);
        }

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
            u[k].setKey(ElGamalAsymKeyFactory.create(false));
            SyncManager<User> em = new UserSyncManagerImpl();
            em.begin();
            em.persist(u[k]);
            em.end();

            Authentifier auth = Application.getInstance().getAuth();
            LoginToken token = new LoginToken();
            token.setToken(auth.getToken(logins[k], passwords[k]));
            token.setUserid(u[k].getId());
        }
        */
    }

}
