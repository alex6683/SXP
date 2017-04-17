package protocol.impl.blockChain;

import org.ethereum.solidity.compiler.SolidityCompiler;

/**
 * Created by alex on 17/04/17.
 */
public class CompileContract {
    private String contract ;

    public CompileContract(String c) {
        contract = c ;
    }

    public void compiler() {
        byte[] contractByte = contract.getContractSrc().getBytes() ;
        SolidityCompiler.R
    }
}
