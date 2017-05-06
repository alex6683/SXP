package protocol.impl.blockChain;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.Users;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import crypt.impl.signatures.EthereumSignature;
import crypt.impl.signatures.EthereumSigner;
import model.entity.ContractEntity;
import model.entity.EthereumKey;
import model.entity.User;
import protocol.api.EstablisherContract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 21/03/17.
 */
public class BlockChainContract extends EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner>{

    EthereumContract ethContract ;

    private String id ;
    private ArrayList<EthereumKey> parties ;
    private ArrayList<String> clauses ;
    private HashMap<EthereumKey, String> partieName ;
    private HashMap<EthereumKey, EthereumSignature> signatures ;
    private EthereumSigner signer;

    public BlockChainContract(ContractEntity contract, EthereumContract ethContract) {
        super() ;
        super.contract = contract ;
        id = contract.getId() ;
        setParties(contract.getParties());
        setClauses(contract.getClauses()) ;
        signer = new EthereumSigner(ethContract) ;
    }

    public void setParties(ArrayList<String> partiesEntity){
        for (String part : partiesEntity){
            JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
            Users users = new Users();
            User user = json.toEntity(users.get(part));
            this.parties.add(user.getEthKeys());
        }
    }

    public void setClauses(ArrayList<String> clausesEntity){
        clauses.addAll(clausesEntity) ;
    }

    @Override
    public void addSignature(EthereumKey k, EthereumSignature s) {

    }

    @Override
    public boolean isFinalized() {
        return false;
    }

    @Override
    public boolean checkContrat(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> contrat) {
        return false;
    }

    @Override
    public boolean equals(EstablisherContract<BigInteger, EthereumKey, EthereumSignature, EthereumSigner> c) {
        return false;
    }

    @Override
    public byte[] getHashableData() {
        return new byte[0];
    }

    @Override
    public EthereumSignature sign(EthereumSigner signer, EthereumKey k) {
        return null;
    }
}
