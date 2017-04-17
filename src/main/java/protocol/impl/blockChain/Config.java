package protocol.impl.blockChain;

import org.springframework.context.annotation.Bean;

/**
 * Created by methylhaine on 18/04/17.
 */
public class Config extends BlockChainRopstenConfig.RopstenSampleConfig {

    @Bean
    public BlockChainRopstenConfig sampleBean() {
        return new BlockChainRopstenConfig();
    }
}
