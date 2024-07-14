package net.thevpc.halfa.engine.render.server;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NSession nSession() {
        // Initialize and return the NSession object
        // Ensure that the Nuts workspace is properly initialized
        try {
            return Nuts.openWorkspace();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize NSession", e);
        }
    }

    @Bean
    public HEngine hEngine(NSession session) {
        // Initialize and return the HEngine object using the provided NSession
        return new HEngineImpl(session);
    }
}
