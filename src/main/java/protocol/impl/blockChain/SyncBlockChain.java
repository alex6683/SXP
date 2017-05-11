package protocol.impl.blockChain;

import java.util.concurrent.atomic.AtomicBoolean;

import org.ethereum.facade.Ethereum;
import org.ethereum.config.SystemProperties ;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.listener.EthereumListenerAdapter;

/**
 * Created by alex on 19/04/17.
 */
public class SyncBlockChain implements Runnable {

    private Ethereum ethereum = null;
    private Class config;
    private AtomicBoolean isSyncDone = new AtomicBoolean(false);

    public SyncBlockChain(Class conf) {
        config = conf;
    }

    public boolean getIsSyncDone() {
        return isSyncDone.get();
    }
    public Ethereum getEthereum() {
        return ethereum;
    }

    public void closeSync() {
        isSyncDone.set(false);
        ethereum.close();
        return ;
    }

    @Override
    public void run() {

        if (!SystemProperties.getDefault().blocksLoader().equals("")) {
            SystemProperties.getDefault().setSyncEnabled(false);
            SystemProperties.getDefault().setDiscoveryEnabled(false);
        }

        ethereum = EthereumFactory.createEthereum(config);

        ethereum.addListener(new EthereumListenerAdapter() {
            @Override
            public void onSyncDone(SyncState state) {
                isSyncDone.set(true) ;
            }
        });
    }
}
