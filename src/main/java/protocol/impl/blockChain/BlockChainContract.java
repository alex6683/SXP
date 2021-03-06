package protocol.impl.blockChain;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.Users;
import controller.tools.JsonTools;
import crypt.api.signatures.Signer;
import crypt.impl.hashs.SHA256Hasher;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.api.Status;
import model.entity.ContractEntity;
import model.entity.EthereumKey;
import model.entity.User;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;
import protocol.api.EstablisherContract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alex on 21/03/17.
 */
public class BlockChainContract extends EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner>{

    private String id ;
    private Date date ;
    private ArrayList<EthereumKey> parties = new ArrayList<>();
    private ArrayList<String> clauses = new ArrayList<>();
    private HashMap<EthereumKey, String> partiesName = new HashMap<>();
    private HashMap<EthereumKey, EthereumSignature> signatures = new HashMap<>();

    private EthereumSigner signer ;

    public BlockChainContract(ContractEntity contract) {
        super() ;
        super.contract = contract ;
        date = contract.getCreatedAt() ;
        setParties(contract.getParties());
        setClauses(contract.getClauses()) ;
        id = ByteUtil.toHexString(getHashableData()) ;
        contract.setTitle(id);
    }

    @Deprecated
    public BlockChainContract(ContractEntity contract, ArrayList<EthereumKey> part) {
        super() ;
        super.contract = contract ;
        date = contract.getCreatedAt() ;
        parties.addAll(part) ;
        int i=0 ;
        for(EthereumKey tmp : part) {
            partiesName.put(tmp, contract.getParties().get(i)) ;
            i++ ;
        }
        setClauses(contract.getClauses()) ;
        id = getHashableData().toString() ;
        contract.setTitle(id);
    }

    // TODO : JsonTools probleme à régler afin de supprimer Deprecated.
    public void setParties(ArrayList<String> partiesEntity){
        for (String part : partiesEntity){
            JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
            User user = json.toEntity(part) ;
            //Users users = new Users();
            //System.out.println("USERS ? " + user.getId()) ;
            //User user = json.toEntity(users.get(part));
            //System.out.println("User ID ENTITY " + user.getId()) ;
            //this.parties.add(user.getEthKeys());
            //this.partiesName.put(user.getEthKeys(), user.getId());
        }
    }

    public void setClauses(ArrayList<String> clausesEntity){
        clauses.addAll(clausesEntity) ;
    }

    public void setSigner(EthereumSigner establisherSigner) {
        signer = establisherSigner ;
    }

    //GETTERS////
    public String getId() { return id; }
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


    @SuppressWarnings("Since15")
    @Override
    public void addSignature(EthereumKey k, EthereumSignature s) {
        if(k == null || !this.parties.contains(k)) {
            throw new RuntimeException("invalid key");
        }
        signatures.putIfAbsent(k, s);
        contract.getSignatures().put(this.partiesName.get(k), s.toString());
    }

    @Override
    public boolean isFinalized() {
        if(signer == null) {
            throw new NullPointerException("Signer not initialized yet") ;
        }
        for(EthereumKey key : parties) {
            if(!signatures.containsKey(key)) {
                return false ;
            }
        }
        for(EthereumSignature partSign : signatures.values()) {
            if(!signer.verify(new byte[0], partSign)) {
                return false ;
            }
        }
        return true ;
    }

    @Override
    public boolean checkContrat(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> contrat) {
        if(!this.equals(contrat) && !this.isFinalized())
            return false;
        setStatus(Status.FINALIZED) ;
        System.out.println("\n[CONTRACT FINALIZED]\n") ;
        return true ;
    }

    @Override
    public boolean equals(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> c) {
        if(!c.getHashableData().equals(getHashableData()))
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
        EthereumSignature signature = signer.sign(new byte[0]) ;

        System.out.println("\n\n[Signature done] : " + k.toString() + "\n\n");

        if(signature == null) {
            throw new NullPointerException("Signature de " + k.getPublicKey() + "impossible");
        }
        addSignature(k, signature);
        return signature ;
    }
}
