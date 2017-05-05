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

    private ArrayList<EthereumKey> parties ;
    private HashMap<EthereumKey, EthereumSignature> signatures ;
    private Signable<EthereumSignature> clauses ;
    private EthereumSigner signer;

    public BlockChainContract(ContractEntity contract, EthereumContract ethContract) {
        super() ;
        this.contract = contract ;
        setParties(contract.getParties());
        signer = new EthereumSigner(ethContract) ;
    }

    public void setParties(ArrayList<String> s){
        for (String u : s){
            JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
            Users users = new Users();
            User user = json.toEntity(users.get(u));
            this.parties.add(user.getEthKeys());
        }
    }

    public void setClauses(Signable<EthereumSignature> c){
        this.clauses = c;
        ArrayList<String> a = new ArrayList<String>();
        a.add(new String(c.getHashableData()));
        this.contract.setClauses(a);
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
