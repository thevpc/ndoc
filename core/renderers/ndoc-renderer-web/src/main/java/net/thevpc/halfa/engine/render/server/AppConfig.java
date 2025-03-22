package net.thevpc.ndoc.engine.render.server;

import net.thevpc.ndoc.api.HEngine;
import net.thevpc.ndoc.engine.DefaultHEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HEngine hEngine() {
        return new DefaultHEngine();
    }
}
