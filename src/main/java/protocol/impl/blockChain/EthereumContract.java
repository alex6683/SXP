package protocol.impl.blockChain;

import org.ethereum.facade.Ethereum ;
import org.ethereum.core.CallTransaction;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;

import java.io.IOException;

/**
 * Created by alex on 18/04/17.
 */
public class EthereumContract {
    private String contractSrc ;
    private CompilationResult.ContractMetadata contractMetadata ;
    private byte[] contractAdr ;

    public EthereumContract(String src) {
        contractSrc = src ;
        contractMetadata = null ;
        contractAdr = null ;
    }

    //GETTERS//
    public String getContractSrc() {
        return contractSrc;
    }
    public CompilationResult.ContractMetadata getContractABI() {
        return contractMetadata;
    }
    public byte[] getContractAdr() {
        return contractAdr;
    }
    ///////////

    //SETTERS//
    public void setContractMetadata(CompilationResult.ContractMetadata contractMetadata) {
        this.contractMetadata = contractMetadata;
    }
    public void setContractAdr(byte[] contractAdr) {
        this.contractAdr = contractAdr;
    }
    public void setContractSrc(String contractSrc) {
        this.contractSrc = contractSrc;
    }
    ///////////

    //COMPILATION//
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
    public CompilationResult.ContractMetadata compileData(SolidityCompiler.Result compiled) throws  IOException {
        CompilationResult resultCompile = CompilationResult.parse(compiled.output) ;
        if (resultCompile.contracts.isEmpty()) {
            throw new RuntimeException("Compilation failed, no contracts returned:\n" + compiled.errors);
        }

        CompilationResult.ContractMetadata metadata = resultCompile.contracts.values().iterator().next();
        if (metadata.bin == null || metadata.bin.isEmpty()) {
            throw new RuntimeException("Compilation failed, no binary returned:\n" + compiled.errors);
        }

        setContractMetadata(metadata);

        return metadata ;
    }
    /////////////


    public boolean isCompiled() {
        return !(contractMetadata == null) ;
    }
    public boolean isDeployed() {
        return !(contractAdr == null) ;
    }
}
