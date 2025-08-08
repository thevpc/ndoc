package net.thevpc.ntexup.engine.render.server;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.engine.impl.DefaultNTxEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NTxEngine hEngine() {
        return new DefaultNTxEngine();
    }
}
