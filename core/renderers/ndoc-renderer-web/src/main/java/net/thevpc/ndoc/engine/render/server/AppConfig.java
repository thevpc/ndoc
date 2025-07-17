package net.thevpc.ndoc.engine.render.server;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NDocEngine hEngine() {
        return new DefaultNDocEngine();
    }
}
