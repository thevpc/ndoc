package net.thevpc.halfa.app.backend.config;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.DefaultHEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public HEngine hEngine() {
        return new DefaultHEngine();
    }

}
