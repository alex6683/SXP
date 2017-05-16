package protocol.impl;

import controller.Application;
import controller.Users;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.Status;
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
import org.ethereum.core.Transaction;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;
import protocol.api.Establisher;
import protocol.impl.blockChain.*;
import rest.api.Authentifier;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 10/05/17.
 */
public class BlockChainEstablisher extends Establisher<BigInteger, EthereumKey, EthereumSignature, EthereumSigner, BlockChainContract> {

    protected final EstablisherService establisherService =
            (EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);

    protected User establisherUser ;
    private boolean shareTxSign ;

    protected String contractId ;
    protected ArrayList<EthereumKey> othersParties = new ArrayList<>() ;

    protected EthereumContract ethContract ;
    protected SyncBlockChain sync ;
    protected Class conf ;

    //TODO : Check if uris is usefull..

    public BlockChainEstablisher(User user, HashMap<EthereumKey, String> uri) {
        // Matching the uris
        uris = uri;
        //Set User who use Establisher instance
        establisherUser = user ;
        shareTxSign = false ;
    }

    public BlockChainEstablisher(User user, Class config, HashMap<EthereumKey, String> uri) {
        // Mathcing the uris
        uris = uri;
        //Set User who use Establisher instance
        establisherUser = user ;
        shareTxSign = false ;
        conf = config ;
    }

    @Override
    public void initialize(BlockChainContract bcContract) {
        super.contract = bcContract ;
        setOthersParties(contract.getParties());
        contractId = contract.getId() ;
        try {
            ethContract = new EthereumContract(establisherUser.getEthKeys()) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.signer = new EthereumSigner(ethContract, sync) ;

        super.signer.setBcContract(contract);

        super.signer.setKey(establisherUser.getEthKeys()) ;
        bcContract.setSigner(super.signer);
        sync = new SyncBlockChain(conf) ;
        sync.run();
    }

    public void initialize(BlockChainContract bcContract, boolean deploy) {
        this.initialize(bcContract);


        //TODO : Check if good part with contract.getParti() and good contractID.

        establisherService.addListener(new ServiceListener() {
            @Override
            public void notify(Messages messages) {
                String content = messages.getMessage("contract") ;
                switch (content.charAt(0)) {
                    case '1' :
                        if(!ethContract.isDeployed()) {
                            ethContract.setContractAdr(Hex.decode(content.substring(1))) ;
                        }
                        break ;
                    case '2' :
                        String solidityHash = ethContract.gethashSolidity();
                        if(!solidityHash.equals(content.substring(1))) {
                            throw new SecurityException("SoliditySrc aren't equals ") ;
                        }
                        break ;
                    case '3' :
                        String fromWho = messages.getMessage("sourceId");
                        upDateSignatures(contract, fromWho, content.substring(1));
                        if(!shareTxSign) {
                            shareSign();
                        }
                        break ;
                    default: throw new IllegalArgumentException("Sent a bad content") ;
                }
            }
                /*BigInteger otherPart = ByteUtil.bytesToBigInteger(Hex.decode(messages.getMessage("sourceId"))) ;
                String addrContract = messages.getMessage("contract") ;
                if(!ethContract.isDeployed()) {
                    ethContract.setContractAdr(Hex.decode(addrContract)) ;
                }
            }*/
        }, establisherUser.getEthKeys().toString())  ;

        if(deploy && !ethContract.isDeployed()) {
            new DeployContract(sync, ethContract, establisherUser.getEthKeys()).run();
            new SolidityConstructor(
                    sync,
                    ethContract,
                    ByteUtil.bigIntegerToBytes(establisherUser.getEthKeys().getPublicKey()),
                    ByteUtil.bigIntegerToBytes(othersParties.get(0).getPublicKey()),
                    "",
                    "",
                    contract.getClauses().get(0),
                    contract.getClauses().get(1)
            ).run() ;
        }
    }

    @Override
    public void start() {
        if(ethContract.isDeployed()) {
            sendContractAddr();
            sendSolidityHash();
        }
    }

    public void sendContractAddr() {
        if(!ethContract.isDeployed())
            throw new NullPointerException("Couldn't send contract Address, no contracts were deployed") ;

        for(EthereumKey key : othersParties) {
            establisherService.sendContract(
                    contractId,
                    key.toString(),
                    establisherUser.getEthKeys().toString(),
                    "1" + ByteUtil.toHexString(ethContract.getContractAdr())
            );
        }
    }

    public void sendSolidityHash() {
        if(!ethContract.isDeployed())
            throw new NullPointerException("Couldn't send contract Address, no contracts were deployed") ;

        for(EthereumKey key : othersParties) {
            establisherService.sendContract(
                    contractId,
                    key.toString(),
                    establisherUser.getEthKeys().toString(),
                    "2" + ethContract.gethashSolidity().toString()
            );
        }
    }

    public void shareSign() {
        for(EthereumKey key : othersParties) {
            establisherService.sendContract(
                    contractId,
                    key.toString(),
                    establisherUser.getEthKeys().toString(),
                    "3" + contract.getSignatures().get(establisherUser.getEthKeys()).getStringEncoded()
            );
        }
        shareTxSign = true ;
    }

    public void upDateSignatures(BlockChainContract c, String who, String TxSign) {
        contract.getSigner().setSync(sync);
        //if(contract.checkContrat(c)) {
            System.out.println("\n--CONTRACT FINALIZED VERIFIéé--\n");
            for(EthereumKey key : contract.getParties()) {
                if(contract.getPartieName().containsKey(key) && contract.getPartieName().get(key).equals(who))
                    System.out.println("Ajout de la signature de " + key.toString()) ;
                    contract.addSignature(key, new EthereumSignature(TxSign));
            }
       // }
    }
    
    public void sign(BlockChainContract c) {
        sync.run();
        contract.getSigner().setSync(sync);
        contract.sign(super.signer, establisherUser.getEthKeys()) ;
        //TODO : attention
        if(contract.checkContrat(c)) {
            System.out.println("\n--CONTRACT FINALIZED--\n");
            shareSign();
        }
    }

    public void setOthersParties(ArrayList<EthereumKey> parts) {
        for (EthereumKey key : parts)
            if(!key.equals(establisherUser.getEthKeys()))
                othersParties.add(key) ;
    }

    public void stopSync() {
        sync.closeSync();
    }

    public EthereumSigner getSigner() {
        return super.signer ;
    }

}
