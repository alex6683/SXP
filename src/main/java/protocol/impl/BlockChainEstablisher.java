package protocol.impl;

import controller.Application;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.UserSyncManager;
import model.entity.EthereumKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.Peer;
import network.api.ServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.factories.AdvertisementFactory;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;
import protocol.api.Establisher;
import protocol.impl.blockChain.BlockChainContract;
import protocol.impl.blockChain.DeployContract;
import protocol.impl.blockChain.EthereumContract;
import protocol.impl.blockChain.SyncBlockChain;
import rest.api.Authentifier;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by alex on 10/05/17.
 */
public class BlockChainEstablisher extends Establisher<BigInteger, EthereumKey, EthereumSignature, EthereumSigner, BlockChainContract> {

    protected final EstablisherService establisherService =
            (EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);

    protected User establisherUser ;

    protected String contractId ;

    protected EthereumContract ethContract ;
    protected SyncBlockChain sync ;

    public BlockChainEstablisher(User user, HashMap<EthereumKey, String> uri) {
        // Matching the uris
        uris = uri;
        //Set User who use Establisher instance
        establisherUser = user ;
    }

    @Override
    public void initialize(BlockChainContract bcContract) {
        bcContract.getSigner().setKey(establisherUser.getEthKeys());
        super.contract = bcContract ;
        super.signer = contract.getSigner() ;
        ethContract = bcContract.getEthContract() ;
        sync = bcContract.getSync() ;
    }

    public void initialize(BlockChainContract bcContract, boolean deploy) {
        this.initialize(bcContract);

        establisherService.addListener(new ServiceListener() {
            @Override
            public void notify(Messages messages) {
                BigInteger otherPart = ByteUtil.bytesToBigInteger(Hex.decode(messages.getMessage("sourceId"))) ;
                //TODO : Check if good part with contract.getParti().
                String addrContract = messages.getMessage("contract") ;
                if(!ethContract.isDeployed()) {
                    ethContract.setContractAdr(Hex.decode(addrContract)) ;
                }
            }
        }, establisherUser.getEthKeys().toString())  ;

        if(deploy && !ethContract.isDeployed()) {
            new DeployContract(sync, ethContract, establisherUser.getEthKeys()) ;
        }
    }

    @Override
    public void start() {
        final Peer peer=Application.getInstance().getPeer();
        // Sending an advertisement (trick to get the other peer URI)
        EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
        cadv.setTitle("Un Contrat");
        cadv.publish(peer);
    }

    public EthereumSigner getSigner() {
        return super.signer ;
    }

}
