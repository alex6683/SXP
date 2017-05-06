package controller;

import java.util.Properties;

import controller.tools.LoggerUtilities;
import model.syncManager.UserSyncManagerImpl;
import network.api.Peer;
import network.factories.PeerFactory;
import protocol.impl.blockChain.*;
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

	public static void main(String[] args) throws InterruptedException {
		//new Application();
		//Application.getInstance().runForTests(8081);

		SyncBlockChain sync = new SyncBlockChain(Config.class) ;
		
		sync.run() ;

		EthereumContract contract = new EthereumContract(SolidityContract.soliditySrc) ;

		new DeployContract(sync, contract).run() ;

		new CallConstructor(sync, contract,
				"49a337147d9249ffe437a780fd6ba1ffd3e2bdad",
				"0f3bce1d0d5bf08310ca3965260b6d0ae3e5b06f",
				"velo",
				"carotte",
				"IZI",
				"OKLM"
				).run() ;


		/*CallGetSign call = new CallGetSign(sync, contract, "getU1") ;
		call.run();
		Object obj = call.getSign() ;
		System.out.println("\n\nSIGN = " + obj +"\n\n") ;

		new CallSetSign(sync, contract, "signatureUser1").run() ;

		call.run();
		obj = call.getSign() ;
		System.out.println("\n\nSIGN = " + obj +"\n\n") ;*/


		//System.out.println("\n\nContract MODIFIED with Constructor !\n\n");

		Thread.currentThread().sleep(5000);
		sync.closeSync();
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
