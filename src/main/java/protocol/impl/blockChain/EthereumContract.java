package protocol.impl.blockChain;

import crypt.impl.hashs.SHA256Hasher;
import model.entity.EthereumKey;
import org.ethereum.crypto.ECKey;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;

import java.io.IOException;

/**
 * Created by alex on 18/04/17.
 */
public class EthereumContract {

    // TODO : Changer sender de class (SendTx ou Signer?)

    private String contractSrc ;
    private String hashSolidity ;
    @Deprecated
    private ECKey sender ;
    private CompilationResult.ContractMetadata contractMetadata ;
    private byte[] contractAdr ;

    //Constructor with default Solidity Src
    @Deprecated
    public EthereumContract() throws IOException {
        contractSrc = new SolidityContract().soliditySrc ;
        contractMetadata = null ;
        contractAdr = null ;
        hashSolidity = ByteUtil.toHexString(new SHA256Hasher().getHash(contractSrc.getBytes())) ;
        sender = ECKey.fromPrivate(
                Hex.decode("287fc6941394e06872850966e20fe190ad43b3d0a3caa82e42cd077a6aaeb8b5")
        );
        this.compileData(this.compileResult()) ;
    }

    //Constructor with default Solidity Src and your sign keys
    public EthereumContract(EthereumKey keys) throws IOException {
        contractSrc = new SolidityContract().soliditySrc ;
        contractMetadata = null ;
        contractAdr = null ;
        hashSolidity = ByteUtil.toHexString(new SHA256Hasher().getHash(contractSrc.getBytes())) ;
        sender = keys.getPrivECKey() ;
        this.compileData(this.compileResult()) ;
    }

    //Constructor with your own Solidity Src and your sign keys
    public EthereumContract(String src, EthereumKey keys) throws IOException {
        contractSrc = src ;
        contractMetadata = null ;
        contractAdr = null ;
        hashSolidity = ByteUtil.toHexString(new SHA256Hasher().getHash(contractSrc.getBytes())) ;
        sender = ECKey.fromPrivate(keys.getPrivateKey()) ;
        this.compileData(this.compileResult()) ;
    }

    //GETTERS//
    public String getContractSrc() {
        return contractSrc;
    }
    public CompilationResult.ContractMetadata getContractMetadata() {
        return contractMetadata;
    }
    public byte[] getContractAdr() {
        return contractAdr;
    }
    public ECKey getSender() {
        return sender ;
    }
    public String gethashSolidity() {
        return hashSolidity;
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
        return isCompiled() && !(contractAdr == null) ;
    }
}
