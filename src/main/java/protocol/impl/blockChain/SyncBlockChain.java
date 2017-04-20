package protocol.impl.blockChain;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ethereum.config.BlockchainConfig;
import org.ethereum.config.BlockchainNetConfig;
import org.ethereum.config.net.RopstenNetConfig;
import org.ethereum.config.net.TestNetConfig;
import org.ethereum.facade.Ethereum;
import org.ethereum.config.SystemProperties ;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.listener.EthereumListenerAdapter;

/**
 * Created by alex on 19/04/17.
 */
public class SyncBlockChain implements Runnable {

    private Ethereum ethereum = null ;
    private Config config = null ;
    private AtomicBoolean isSyncDone = new AtomicBoolean(false) ;

    public SyncBlockChain() {} ;
    public SyncBlockChain(Config conf) {
        config = conf ;
    }

    public boolean getIsSyncDone() {
        return isSyncDone.get() ;
    }

    @Override
    public void run() {

        if (!SystemProperties.getDefault().blocksLoader().equals("")) {
            SystemProperties.getDefault().setSyncEnabled(false);
            SystemProperties.getDefault().setDiscoveryEnabled(false);
        }

        ethereum = EthereumFactory.createEthereum() ;

        ethereum.addListener(new EthereumListenerAdapter() {
            //true when BlockChain is Sync
            public void onSyncDone() {
                isSyncDone.set(true);
            }
        });

    }
}
