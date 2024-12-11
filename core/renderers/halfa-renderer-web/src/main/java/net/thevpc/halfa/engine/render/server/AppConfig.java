package net.thevpc.halfa.engine.render.server;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HEngine hEngine() {
        return new HEngineImpl();
    }
}
