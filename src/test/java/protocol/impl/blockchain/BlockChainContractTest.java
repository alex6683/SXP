package protocol.impl.blockchain;

import model.entity.ContractEntity;
import org.junit.Test;
import protocol.impl.blockChain.BlockChainContract;

import java.util.ArrayList;

/**
 * Created by alex on 09/05/17.
 */
public class BlockChainContractTest {

    private ContractEntity contractEntity ;
    private BlockChainContract bcContract ;

    private ArrayList<String> setEntityContract(String... entity) {
        ArrayList<String> newEntities = new ArrayList<>() ;
        for(String tmp : entity) {
            newEntities.add(tmp) ;
        }
        return newEntities ;
    }

    @Test
    public void test() {
        contractEntity.setParties(setEntityContract(""));
    }
}
