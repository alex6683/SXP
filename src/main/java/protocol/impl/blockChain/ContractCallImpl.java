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
    protected EthereumContract contract ;

    public ContractCallImpl(SyncBlockChain ethereum, EthereumContract contract) {
        super(ethereum);
        this.contract = contract ;
        contractCall = new CallTransaction.Contract(contract.getContractMetadata().abi);
    }

    public void contractBlockChainConstructor(Object ...args) throws Exception {
        CallTransaction.Function function = contractCall.getConstructor();
        if (function.type == CallTransaction.FunctionType.constructor)
            System.out.println("///////////// Constructeur trouvé /////////////////////////////////" + function.type.toString() + function.type.name());
        byte[] functionCallBytes = function.encode(args);
        TransactionReceipt receipt1 = sendTxAndWait(contract.getSender(), contract.getContractAdr(), functionCallBytes);
        if (!receipt1.isSuccessful()) {
            System.err.println("Some troubles launching constructor: " + receipt1.getError() + contractCall.getConstructor().name);
            return;
        }
    }

    //Call contract Constructor on blockChain
   /* public void contractBlockChainConstructor(String user1, String user2, String itemU1, String itemU2, String clause1, String clause2) throws Exception {
        CallTransaction.Function Sign = contractCall.getConstructor() ;
        if (Sign.type == CallTransaction.FunctionType.constructor)
            System.out.println("///////////// Constructeur trouvé /////////////////////////////////" + Sign.type.toString() + Sign.type.name());

        System.out.println(" c bon ?" + user1 + " / " +  user2 + " / " + itemU1 + " / " + itemU2 +  " / " + clause1 + " / " + clause2);

        byte[] functionCallBytes = Sign.encode(
                user1,
                user2,
                itemU1,
                itemU2,
                clause1,
                clause2
        );

        TransactionReceipt receipt1 = sendTxAndWait(contract.getSender(), contract.getContractAdr(), contractCall.getConstructor().encode(user1, user2, itemU1, itemU2, clause1, clause2));
        if (!receipt1.isSuccessful()) {
            System.err.println("Some troubles launching constructor: " + receipt1.getError() + contractCall.getConstructor().name);
            return;
        }
    } */

    //Call function of our contract
    public void callFunc(String func, Object ...args) throws Exception {
        CallTransaction.Function fct = contractCall.getByName(func);
        byte[] functionCallBytes = fct.encode(args);
        TransactionReceipt receipt2 = sendTxAndWait(contract.getSender(), contract.getContractAdr(), functionCallBytes);
        if (!receipt2.isSuccessful()) {
            System.err.println("Some troubles calling a contract function : " + receipt2.getError());
            return;
        }
    }

    //Return value of get function of our contract
    public Object getReturnContract(String functionName) throws Exception {
        ProgramResult r = sync.getEthereum().callConstantFunction(Hex.toHexString(contract.getContractAdr()),
                contractCall.getByName(functionName));
        Object[] ret = contractCall.getByName(functionName).decodeResult(r.getHReturn());
        return ret[0] ;
    }
}
