package net.thevpc.ndoc.engine.log;

import net.thevpc.ndoc.api.log.NDocMsg;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.NOut;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import java.time.Instant;
import java.util.logging.Level;

public class DefaultNDocLogger implements NDocLogger {
    private NDocResource defaultSource;

    public DefaultNDocLogger() {

    }
    public DefaultNDocLogger(NDocResource defaultSource) {
        this.defaultSource = defaultSource;
    }
    @Override
    public void log(NMsg message) {
        log(NDocMsg.of(message));
    }

    @Override
    public void log(NMsg message, NDocResource source) {
        log(NDocMsg.of(message, source));
    }

    @Override
    public void log(NDocMsg msg) {
        Instant time = Instant.now();
        NMsg nmsg=msg.message();
        Level type=nmsg.getLevel();
        Throwable error=msg.error();
        NDocResource source=msg.source();
        if (type == null) {
            type = Level.INFO;
        }
        if (source == null) {
            source = defaultSource;
        }
        NOut.println(NMsg.ofC("[%s] [%s] [%s] %s", time, type,
                source == null ? null : source.shortName(),
                nmsg
        ));
        if (error != null) {
            for (String s : NStringUtils.stacktraceArray(error)) {
                NOut.println(NMsg.ofC("\t%s", s));
            }
        }
    }
}
