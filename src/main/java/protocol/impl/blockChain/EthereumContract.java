package protocol.impl.blockChain;

import org.ethereum.core.CallTransaction;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;

import java.io.IOException;

/**
 * Created by alex on 18/04/17.
 */
public abstract class EthereumContract {
    private String contractSrc ;
    private byte[] contractAdr ;
    private CallTransaction.Contract contractABI ;

    public EthereumContract(String src) {
        contractSrc = src ;
        contractAdr = null ;
        contractABI = null ;
    }

    public String getContractSrc() {
        return contractSrc;
    }

    public SolidityCompiler.Result compileResult() throws IOException{
        if(contractSrc==null) {
            throw new NullPointerException("EthereumContract is empty") ;
        }
        byte[] contractBytes = contractSrc.getBytes() ;
        SolidityCompiler.Result compiled = SolidityCompiler.compile(
                contractBytes,
                true,
                SolidityCompiler.Options.ABI,
                SolidityCompiler.Options.BIN
        ) ;
        if(compiled.isFailed()) {
            throw new RuntimeException("Contract compilation failed:\n" + compiled.errors) ;
        }
        return compiled ;
    }

    public CompilationResult.ContractMetadata compileData() throws  IOException {
        SolidityCompiler.Result compiled = this.compileResult() ;
        CompilationResult resultCompile = CompilationResult.parse(compiled.output) ;
        if (resultCompile.contracts.isEmpty()) {
            throw new RuntimeException("Compilation failed, no contracts returned:\n" + compiled.errors);
        }

        CompilationResult.ContractMetadata metadata = resultCompile.contracts.values().iterator().next();
        if (metadata.bin == null || metadata.bin.isEmpty()) {
            throw new RuntimeException("Compilation failed, no binary returned:\n" + compiled.errors);
        }
        return metadata ;
    }
}
