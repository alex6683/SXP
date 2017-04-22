package protocol.impl.blockChain;

import com.typesafe.config.ConfigFactory;
import org.ethereum.config.SystemProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by methylhaine on 18/04/17.
 */
public class Config {

    private final String configuration =
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
                    "database.dir = database-ropsten";

    //Créé deux affichage de config
    @Bean
    public SystemProperties systemProperties() {
        SystemProperties props = new SystemProperties();
        props.overrideParams(ConfigFactory.parseString(configuration.replaceAll("'", "\"")));
        return props;
    }


    //A voir
    private static class ConfigTest {
        @Bean
        public Config basicSample() {
            return new Config();
        }
    }

}
