package protocol.impl.blockChain;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.*;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.listener.EthereumListener;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.message.Message;
import org.ethereum.net.p2p.HelloMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.ethereum.samples.BasicSample;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import com.typesafe.config.ConfigFactory;
import org.ethereum.config.SystemProperties;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by methylhaine on 18/04/17.
 */
public class BlockChainRopstenConfig extends BasicSample{

    protected abstract static class RopstenSampleConfig {
        private final String config =
                "peer.discovery = {" +
                        "    enabled = true \n" +
                        "    ip.list = [" +
                        "        '94.242.229.4:40404'," +
                        "        '94.242.229.203:30303'" +
                        "    ]" +
                        "} \n" +
                        "peer.p2p.eip8 = true \n" +
                        "peer.networkId = 3 \n" +
                        "sync.enabled = true \n" +
                        "genesis = ropsten.json \n" +
                        "blockchain.config.name = 'ropsten' \n" +
                        "database.dir = database-ropstenSample";

        @Bean
        public SystemProperties systemProperties() {
            SystemProperties props = new SystemProperties();
            props.overrideParams(ConfigFactory.parseString(config.replaceAll("'", "\"")));
            return props;
        }
    }
}
