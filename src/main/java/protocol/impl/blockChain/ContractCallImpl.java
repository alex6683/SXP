package protocol.impl.blockChain;

import model.entity.EthereumKey;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.vm.program.ProgramResult;
import org.spongycastle.util.encoders.Hex;

/**
 * Created by alex on 25/04/17.
 */
public class ContractCallImpl extends SendTransaction {

    private CallTransaction.Contract contractCall ;
    protected EthereumContract contract ;


    public ContractCallImpl(SyncBlockChain ethereum, EthereumContract contract) {
        super(ethereum);
        this.contract = contract ;
        contractCall = new CallTransaction.Contract(contract.getContractMetadata().abi);
    }

    public ContractCallImpl(SyncBlockChain ethereum, EthereumContract contract, EthereumKey keys) {
        super(ethereum, keys);
        this.contract = contract ;
        contractCall = new CallTransaction.Contract(contract.getContractMetadata().abi);
    }

    //getConstructor renvoie bad jump Exception avec solc 0.4.10.
    //A voir avec version ultérieur
    /*public void contractBlockChainConstructor(Object ...args) throws Exception {
        CallTransaction.Function function = contractCall.getConstructor();
        if (function.type == CallTransaction.FunctionType.constructor)
            System.out.println("///////////// Constructeur trouvé /////////////////////////////////" + function.type.toString() + function.type.name());
        byte[] functionCallBytes = function.encode(args);
        TransactionReceipt receipt1 = sendTxAndWait(contract.getSender(), contract.getContractAdr(), functionCallBytes);
        if (!receipt1.isSuccessful()) {
            System.err.println("Some troubles launching constructor: " + receipt1.getError() + contractCall.getConstructor().name + " Function : " + function.type.name());
            return;
        }
    }*/

    //Call function of our contract
    public TransactionReceipt callFunc(String func, Object ...args) throws Exception {
        CallTransaction.Function fct = contractCall.getByName(func);
        byte[] functionCallBytes = fct.encode(args);
        TransactionReceipt receipt2 = sendTxAndWait(contract.getContractAdr(), functionCallBytes);
        if (!receipt2.isSuccessful()) {
            System.err.println("Some troubles calling a contract function : " + receipt2.getError() + " Function : " + func);
            return null;
        }
        return receipt2 ;
    }

    //Return value of get function of our contract
    public Object getReturnContract(String functionName) throws Exception {
        ProgramResult r = sync.getEthereum().callConstantFunction(Hex.toHexString(contract.getContractAdr()),
                contractCall.getByName(functionName));
        Object[] ret = contractCall.getByName(functionName).decodeResult(r.getHReturn());
        return ret[0] ;
    }
}
