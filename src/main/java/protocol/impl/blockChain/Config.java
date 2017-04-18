package protocol.impl.blockChain;

import org.ethereum.config.SystemProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by methylhaine on 18/04/17.
 */
public class Config extends BlockChainRopstenConfig.RopstenSampleConfig {

    @Bean
    public BlockChainRopstenConfig sampleBean() {
        //System.out.println("////// Configuration en cours.. ///////");
        return new BlockChainRopstenConfig();
    }
}
