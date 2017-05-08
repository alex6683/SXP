package protocol.impl.blockChain;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.Users;
import controller.tools.JsonTools;
import crypt.impl.hashs.SHA256Hasher;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.Status;
import model.entity.ContractEntity;
import model.entity.EthereumKey;
import model.entity.User;
import protocol.api.EstablisherContract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alex on 21/03/17.
 */
public class BlockChainContract extends EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner>{

    private EthereumContract ethContract ;
    private SyncBlockChain sync ;

    private String id ;
    private Date date ;
    private ArrayList<EthereumKey> parties ;
    private ArrayList<String> clauses ;
    private HashMap<EthereumKey, String> partiesName ;
    private HashMap<EthereumKey, EthereumSignature> signatures ;
    private EthereumSigner signer;

    public BlockChainContract(ContractEntity contract, EthereumContract ethContract, SyncBlockChain sync) {
        super() ;
        super.contract = contract ;
        date = contract.getCreatedAt() ;
        setParties(contract.getParties());
        setClauses(contract.getClauses()) ;
        id = new String(this.getHashableData()) ;
        signer = new EthereumSigner(ethContract, sync) ;
    }

    public void setParties(ArrayList<String> partiesEntity){
        for (String part : partiesEntity){
            JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
            Users users = new Users();
            User user = json.toEntity(users.get(part));
            this.parties.add(user.getEthKeys());
            this.partiesName.put(user.getEthKeys(), user.getId());
        }
    }

    public void setClauses(ArrayList<String> clausesEntity){
        clauses.addAll(clausesEntity) ;
    }

    //GETTERS////
    public EthereumContract getEthContract() {
        return ethContract;
    }
    public String getId() {
        return id;
    }
    public ArrayList<EthereumKey> getParties() {
        return parties;
    }
    public ArrayList<String> getClauses() {
        return clauses;
    }
    public HashMap<EthereumKey, EthereumSignature> getSignatures() {
        return signatures;
    }
    public HashMap<EthereumKey, String> getPartieName() {
        return partiesName;
    }
    public EthereumSigner getSigner() {
        return signer;
    }
    ///////////


    @Override
    public void addSignature(EthereumKey k, EthereumSignature s) {
        if(k == null || !this.parties.contains(k)) {
            throw new RuntimeException("invalid key");
        }
        signatures.put(k, s);
        contract.getSignatures().put(this.partiesName.get(k), s.toString());
    }

    @Override
    public boolean isFinalized() {
        for(EthereumSignature partSign : signatures.values()) {
            if(!signer.verify(new byte[0], partSign)) {
                return false ;
            }
        }
        return true ;
    }

    @Override
    public boolean checkContrat(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> contrat) {
        if(!this.equals(contrat) && this.isFinalized())
            return false;
        setStatus(Status.FINALIZED);
        return true ;
    }

    @Override
    public boolean equals(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> c) {
        if(!c.getHashableData().equals(this.getHashableData()))
            return false;
        return true ;
    }

    @Override
    public byte[] getHashableData() {
        String hashParties = parties.toString() ;
        String hashClauses = clauses.toString() ;
        String concat = hashParties + hashClauses + date.toString() ;
        return new SHA256Hasher().getHash(concat.getBytes()) ;
    }

    @Override
    public EthereumSignature sign(EthereumSigner signer, EthereumKey k) {
        setStatus(Status.SIGNING);
        return signer.sign(new byte[0]) ;
    }
}
