package protocol.impl.blockChain;

import jdk.nashorn.internal.objects.annotations.Function;

/**
 * Created by alex on 25/04/17.
 */
public class CallContructor extends ContractCallImpl implements Runnable {

    String part1 ;
    String part2 ;
    String item1 ;
    String item2 ;

    public CallContructor(SyncBlockChain ethereum, EthereumContract contract) {
        super(ethereum, contract);
    }


    @Override
    public void run() {
    }
}
