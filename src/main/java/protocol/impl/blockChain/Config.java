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
                    "        '54.94.239.50:30303'," +
                    "        '52.16.188.185:30303'," +
                    "        '52.74.57.123:30303',"  +
                    "    ]" +
                    "} \n" +
                    "peer.p2p.eip8 = true \n" +
                    "peer.networkId = 3 \n" +
                    "sync.enabled = true \n" +
                    "genesis = ropsten.json \n" +
                    "blockchain.config.name = 'ropsten' \n" +
                    "database.dir = database-ropsten";

    public String getConfiguration(){
        return this.configuration;
    }

    //Crée deux affichages de config
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
