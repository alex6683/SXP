package protocol.impl.blockChain;

import org.ethereum.core.CallTransaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.vm.program.ProgramResult;
import org.spongycastle.util.encoders.Hex;

/**
 * Created by alex on 25/04/17.
 */
public class ContractCallImpl extends SendTransaction {

    private CallTransaction.Contract contractCall ;
    private EthereumContract contract ;

    public ContractCallImpl(SyncBlockChain ethereum, EthereumContract contract) {
        super(ethereum);
        this.contract = contract ;
        contractCall = new CallTransaction.Contract(contract.getContractMetadata().abi);
    }

    //Call contract Constructor on blockChain
    public void contractBlockChainConstructor(String user1, String user2, String itemUser1, String itemUser2) throws Exception {
        CallTransaction.Function Sign = contractCall.getConstructor() ;
        byte[] functionCallBytes = Sign.encode(
                /*user1,
                user2,
                itemUser1,
                itemUser2*/
        );
        TransactionReceipt receipt1 = sendTxAndWait(contract.getSender(), contract.getContractAdr(), functionCallBytes);
    }

    //Call function with No Args of our contract
    public void callFunctNoArgs(String functionName) throws Exception {
        TransactionReceipt receipt2 = sendTxAndWait(contract.getSender(), contract.getContractAdr(),
                contractCall.getByName(functionName).encode());
    }

    //Return value of get function of our contract
    public Object getReturnContract(String functionName) throws Exception {
        ProgramResult r = sync.getEthereum().callConstantFunction(Hex.toHexString(contract.getContractAdr()),
                contractCall.getByName(functionName));
        Object[] ret = contractCall.getByName(functionName).decodeResult(r.getHReturn());
        return ret[0] ;
    }
}
