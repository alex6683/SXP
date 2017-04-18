package controller;

import java.util.Properties;

import controller.tools.LoggerUtilities;
import model.syncManager.UserSyncManagerImpl;
import network.api.Peer;
import network.factories.PeerFactory;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.samples.BasicSample;
import org.ethereum.samples.MordenSample;
import protocol.impl.blockChain.BlockChainImpl;
import protocol.impl.blockChain.BlockChainRopstenConfig;
import protocol.impl.blockChain.Config;
import protocol.impl.blockChain.EthereumContract;
import rest.api.Authentifier;
import rest.factories.AuthentifierFactory;
import rest.factories.RestServerFactory;

/**
 * Main class
 * {@link Application} is a singleton
 * @author Julien Prudhomme
 *
 */
public class Application {
	private static Application instance = null;
	private static UserSyncManagerImpl umg;
	private Peer peer;
	private Authentifier auth;

	public Application() {
		if(instance != null) {
			throw new RuntimeException("Application can be instanciate only once !");
		}
		instance = this;
	}

	public static Application getInstance()	{
		return instance;
	}

	public void run() {
		setPeer(PeerFactory.createDefaultAndStartPeer());
		setAuth(AuthentifierFactory.createDefaultAuthentifier());
		RestServerFactory.createAndStartDefaultRestServer(8080); //start the rest api
	}

	public void runForTests(int restPort) {
		Properties p = System.getProperties();
		p.put("derby.system.home", "./.db-" + restPort + "/");
		umg = new UserSyncManagerImpl(); //just init the db
		umg.close();
		umg = null;
		try {
			setPeer(PeerFactory.createDefaultAndStartPeerForTest());
			setAuth(AuthentifierFactory.createDefaultAuthentifier());
			RestServerFactory.createAndStartDefaultRestServer(restPort);
		} catch (Exception e) {
			LoggerUtilities.logStackTrace(e);
		}		
	}

	public static void main(String[] args) {
		//new Application();
		//Application.getInstance().runForTests(8081);
		try {
			System.out.println("Config Ropsten :");
			EthereumFactory.createEthereum(Config.class);
			EthereumContract contrat = new EthereumContract("contract Signature {" +

					"  struct Contract {" +
					"    string itemU1;" +
					"    string itemU2;" +
					"    string user1;" +
					"    string user2;" +
					"  }" +

					"  bool public signedUser1;" +
					"  bool public signedUser2;" +

					"  Contract public contractSXP;" +

					"  function createContract(string user1, string user2, string itemU1, string itemU2){" +
					"    contractSXP.itemU1 = itemU1;" +
					"    contractSXP.itemU2 = itemU2;" +
					"    contractSXP.user1 = user1;" +
					"    contractSXP.user2 = user2;" +
					"  }" +

					"  function Signature(string user1addr, string user2addr, string itemU1, string itemU2){" +
					//"    if(msg.sender != user1addr)" +
					//"      throw;" +
					"    createContract(user1addr, user2addr, itemU1, itemU2);" +
					"      signedUser1 = false;" +
					"      signedUser2 = false;" +
					"  }" +

					"  function getU1() constant returns (bool) {" +
					"    return signedUser1;" +
					"  }" +

					"  function getU2() constant returns (bool) {" +
					"    return signedUser2;" +
					"  }" +

					"  function signatureUser1(){" +
					//"    if(msg.sender != contractSXP.user1)" +
					//"      throw;" +
					"    signedUser1 = true;" +
					"  }" +

					"  function signatureUser2(){" +
					//"    if(msg.sender != contractSXP.user2)" +
					//"      throw;" +
					"    signedUser2 = true;" +
					"  }" +

					"}" ) ;

			contrat.compileData(contrat.compileResult()) ;

			System.out.println("COMPILATION ?? " + contrat.compileResult().isFailed()) ;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void stop(){
		peer.stop();
		instance = null;
	}
	
	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	public Authentifier getAuth() {
		return auth;
	}

	public void setAuth(Authentifier auth) {
		this.auth = auth;
	}
}
