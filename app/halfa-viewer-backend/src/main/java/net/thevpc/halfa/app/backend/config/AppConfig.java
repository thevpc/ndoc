package net.thevpc.halfa.app.backend.config;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HEngine hEngine(NSession session) {
        // Initialize and return the HEngine object using the provided NSession
        return new HEngineImpl(session);
    }
}
