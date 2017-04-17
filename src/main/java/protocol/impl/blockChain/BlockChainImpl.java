package protocol.impl.blockChain;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.crypto.ECKey;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.ethereum.solidity.compiler.CompilationResult.ContractMetadata;
import org.ethereum.solidity.compiler.SolidityCompiler.Result;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.program.ProgramResult;
import org.spongycastle.util.encoders.Hex;

public class BlockChainImpl {

    private Ethereum ethereum ;

    ECKey sender = ECKey.fromPrivate(Hex.decode("287fc6941394e06872850966e20fe190ad43b3d0a3caa82e42cd077a6aaeb8b5"));

    private static boolean isSyncDone = false ;

    private Map<ByteArrayWrapper, TransactionReceipt> txWaiters =
            Collections.synchronizedMap(new HashMap<ByteArrayWrapper, TransactionReceipt>());

    //Contract Src to compile and deploy
    private String contractSrc =

            "contract Signature {" +

                    "  struct Contract {" +
                    "    string itemU1;" +
                    "    string itemU2;" +
                    "    string user1;" +
                    "    string user2;" +
                    "  }" +

                    "  bool public signedUser1;" +
                    "  bool public signedUser2;" +

                    "  Contract public contractSXP;" +

                    "  function createContract(string user1, string user2, string itemU1, string itemU2){" +
                    "    contractSXP.itemU1 = itemU1;" +
                    "    contractSXP.itemU2 = itemU2;" +
                    "    contractSXP.user1 = user1;" +
                    "    contractSXP.user2 = user2;" +
                    "  }" +

                    "  function Signature(string user1addr, string user2addr, string itemU1, string itemU2){" +
                    //"    if(msg.sender != user1addr)" +
                    //"      throw;" +
                    "    createContract(user1addr, user2addr, itemU1, itemU2);" +
                    "      signedUser1 = false;" +
                    "      signedUser2 = false;" +
                    "  }" +

                    "  function getU1() constant returns (bool) {" +
                    "    return signedUser1;" +
                    "  }" +

                    "  function getU2() constant returns (bool) {" +
                    "    return signedUser2;" +
                    "  }" +

                    "  function signatureUser1(){" +
                    //"    if(msg.sender != contractSXP.user1)" +
                    //"      throw;" +
                    "    signedUser1 = true;" +
                    "  }" +

                    "  function signatureUser2(){" +
                    //"    if(msg.sender != contractSXP.user2)" +
                    //"      throw;" +
                    "    signedUser2 = true;" +
                    "  }" +

                    "}" ;

    private byte[] contractAddr = null ;

    private CallTransaction.Contract contractABI ;

    ///////////////////////////////////////////////

    public String getContractSrc() {
        return contractSrc ;
    }

    public CallTransaction.Contract getContractABI() {
        return contractABI ;
    }

    public byte[] getContractAddr() {
        return contractAddr ;
    }

    public void setContractSrc(String contract) {
        this.contractSrc = contract ;
    }

    public void setContractAdr(byte[] address) {
        contractAddr = address ;
    }

    public void setContractABI(CallTransaction.Contract contract) {
        contractABI = contract ;
    }

    /////////////////////////////////////////////

    //Compile contract Src and check Result compilation
    public Result compileContrat(String contrat) throws IOException {
        byte[] contratBytes = contrat.getBytes() ;
        SolidityCompiler.Result resultat = SolidityCompiler.compile(contratBytes, true, SolidityCompiler.Options.ABI, SolidityCompiler.Options.BIN) ;
        if (resultat.isFailed()) {
            throw new RuntimeException("Contract compilation failed:\n" + resultat.errors);
        }
        return resultat ;
    }

    //Create Metadata to deploy on BlockChain
    public ContractMetadata createData(Result contratCompiler) throws IOException {
        CompilationResult resultCompil = CompilationResult.parse(contratCompiler.output);
        if (resultCompil.contracts.isEmpty()) {
            throw new RuntimeException("Compilation failed, no contracts returned:\n" + contratCompiler.errors);
        }
        CompilationResult.ContractMetadata metadata = resultCompil.contracts.values().iterator().next();
        if (metadata.bin == null || metadata.bin.isEmpty()) {
            throw new RuntimeException("Compilation failed, no binary returned:\n" + contratCompiler.errors);
        }
        return metadata ;
    }

    //Deploy contract and Wait Transaction include
    public TransactionReceipt sendTxAndWait(ECKey senderAddress, byte[] receiveAddress, byte[] data) throws Exception {
        BigInteger nonce = ethereum.getRepository().getNonce(senderAddress.getAddress());
        Transaction tx = new Transaction(
                ByteUtil.bigIntegerToBytes(nonce),
                ByteUtil.longToBytesNoLeadZeroes(ethereum.getGasPrice()),
                ByteUtil.longToBytesNoLeadZeroes(3_000_000),
                receiveAddress,
                ByteUtil.ZERO_BYTE_ARRAY,
                data,
                ethereum.getChainIdForNextBlock());
        tx.sign(senderAddress);

        ethereum.submitTransaction(tx);

        return waitForTx(tx.getHash());
    }

    //Function call into sendTxAndWait for check each block if Tx include
    public TransactionReceipt waitForTx(byte[] txHash) throws Exception {
        ByteArrayWrapper txHashW = new ByteArrayWrapper(txHash);
        txWaiters.put(txHashW, null);
        long startBlock = ethereum.getBlockchain().getBestBlock().getNumber();
        while(true) {
            TransactionReceipt receipt = txWaiters.get(txHashW);
            if (receipt != null) {
                return receipt;
            } else {
                long curBlock = ethereum.getBlockchain().getBestBlock().getNumber();
                if (curBlock > startBlock + 16) {
                    throw new RuntimeException("The transaction was not included during last 16 blocks: " + txHashW.toString().substring(0,8));
                } else {
                    System.out.println("Waiting for block with transaction 0x" + txHashW.toString().substring(0,8) +
                            " included (" + (curBlock - startBlock) + " blocks received so far) ...");
                }
            }
            synchronized (ethereum.getBlockchain().getBestBlock()) {
                ethereum.getBlockchain().getBestBlock().wait(20000);
            }
        }	}

    //Call contract Constructor on blockChain
    public void contractBlockChainConstructor(String user1, String user2, String itemUser1, String itemUser2) throws Exception {
        CallTransaction.Function Sign = getContractABI().getConstructor() ;
        byte[] functionCallBytes = Sign.encode(
                user1,
                user2,
                itemUser1,
                itemUser2);
        TransactionReceipt receipt1 = sendTxAndWait(sender, getContractAddr(), functionCallBytes);
        System.out.println("Constructeur OK\n\n");
    }

    //Call function with No Args of our contract
    public void callFunctNoArgs(String functionName) throws Exception {
        TransactionReceipt receipt2 = sendTxAndWait(sender, getContractAddr(),
                getContractABI().getByName(functionName).encode());
    }

    //Return value of get function of our contract
    public Object getReturnContract(String functionName) throws Exception {
        ProgramResult r = ethereum.callConstantFunction(Hex.toHexString(getContractAddr()),
                getContractABI().getByName(functionName));
        Object[] ret = getContractABI().getByName(functionName).decodeResult(r.getHReturn());
        return ret[0] ;
    }

    //For deployment and Call constructor
    //Sign for test
    public class deployContract implements Runnable {

        @Override
        public void run() {

            //Config choice
            if (!SystemProperties.getDefault().blocksLoader().equals("")) {
                SystemProperties.getDefault().setSyncEnabled(false);
                SystemProperties.getDefault().setDiscoveryEnabled(false);
            }

            //Ethereum Sync
            ethereum = EthereumFactory.createEthereum();

            //get Ethereum state
            ethereum.addListener(new EthereumListenerAdapter() {

                //Check for each new Block if current Transaction is included in it
                @Override
                public void onBlock(Block block, List<TransactionReceipt> receipts) {
                    for (TransactionReceipt receipt : receipts) {
                        ByteArrayWrapper txHashW = new ByteArrayWrapper(receipt.getTransaction().getHash());
                        if (txWaiters.containsKey(txHashW)) {
                            txWaiters.put(txHashW, receipt);
                            synchronized (this) {
                                notifyAll();
                            }
                        }
                    }
                }

                //true when BlockChain is Sync
                public void onSyncDone() {
                    isSyncDone = true;
                }
            });

            if (!SystemProperties.getDefault().blocksLoader().equals(""))
                ethereum.getBlockLoader().loadBlocks();
            //Wait until blockchain is fully sync
            while (true) {
                if (!isSyncDone) {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {

                    SolidityCompiler.Result result;
                    try {
                        ////////////////////////////////////////////////
                        //COMPILATION
                        System.out.println("\n\nCompilation ... \n\n");
                        result = compileContrat(contractSrc);
                        ContractMetadata metadata = createData(result) ;
                        System.out.println("\n\nCompilation SUCCESS !\n\n");
                        ////////////////////////////////////////////////
                        //SEND Tx
                        TransactionReceipt receipt = sendTxAndWait(sender, null, Hex.decode(metadata.bin)) ;
                        ////////////////////////////////////////////////
                        //GET Receipt Tx and CALL FUNCTION
                        setContractAdr(receipt.getTransaction().getContractAddress()) ;
                        System.out.println("\n\nDeployment SUCCESS ! \ncontractAddr : "
                                + Hex.toHexString(getContractAddr()) + "\n\n");
                        CallTransaction.Contract contract = new CallTransaction.Contract(metadata.abi);
                        setContractABI(contract);
                        ////////////////////////////////////////////////
                        //	Call contract Contructor on BlockChain
                        System.out.println("\n\nCall contract Constructor ...\n\n");
                        contractBlockChainConstructor(
                                "49a337147d9249ffe437a780fd6ba1ffd3e2bdad",
                                "0f3bce1d0d5bf08310ca3965260b6d0ae3e5b06f",
                                "velo",
                                "carotte"
                        );
                        System.out.println("\n\nContract MODIFIED with Constructor !\n\n");
                        ////////////////////////////////////////////////
                        // Check signature init User1 on BlockChain
                        System.out.println("\n\nPeer1 signed init ? : " + getReturnContract("getU1") +"\n\n");
                        ///////////////////////////////////////////////////////////////////
                        //	Call signature function on BlockChain
                        System.out.println("\nPeer1 signing ... \n");
                        callFunctNoArgs("signatureUser1");
                        System.out.println("\nPeer1 signing ... \n");
                        /////////////////////////////////////////////////
                        //	Check contract's modifications (signature User1) on BlockChain
                        System.out.println("\n\nPeer1 signed SUCCESS : " + getReturnContract("getU1") +"\n\n");


                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Exit when execution Successful
                    ethereum.close();
                    System.exit(1);
                }
            }

        }

    }

    //Call signature function and get it
    public class signContract implements Runnable {

        public void run() {
            //Config choice
            if (!SystemProperties.getDefault().blocksLoader().equals("")) {
                SystemProperties.getDefault().setSyncEnabled(false);
                SystemProperties.getDefault().setDiscoveryEnabled(false);
            }

            //Ethereum Sync
            ethereum = EthereumFactory.createEthereum();

            //get Ethereum state
            ethereum.addListener(new EthereumListenerAdapter() {

                //Check for each new Block if current Transaction is included in it
                @Override
                public void onBlock(Block block, List<TransactionReceipt> receipts) {
                    for (TransactionReceipt receipt : receipts) {
                        ByteArrayWrapper txHashW = new ByteArrayWrapper(receipt.getTransaction().getHash());
                        if (txWaiters.containsKey(txHashW)) {
                            txWaiters.put(txHashW, receipt);
                            synchronized (this) {
                                notifyAll();
                            }
                        }
                    }
                }

                //true when BlockChain is Sync
                public void onSyncDone() {
                    isSyncDone = true;
                }
            });

            if (!SystemProperties.getDefault().blocksLoader().equals(""))
                ethereum.getBlockLoader().loadBlocks();
            //Wait until blockchain is fully sync
            while (true) {
                if (!isSyncDone) {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        System.out.println("\nPeer1 signed init ? : " + getReturnContract("getU1") +"\n");
                        callFunctNoArgs("signatureUser1");
                        System.out.println("\nPeer1 signing ... \n");
                        System.out.println("\nPeer1 signed init ? : " + getReturnContract("getU1") +"\n");

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Exit when execution Successful
                    ethereum.close();
                    System.exit(13);
                }
            }
        }
    }

}
