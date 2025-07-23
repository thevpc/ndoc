package net.thevpc.ndoc.api.engine;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.log.NLog;
import net.thevpc.nuts.log.NLogVerb;
import net.thevpc.nuts.util.NMsg;

import java.time.Instant;
import java.util.logging.Level;

public class NDocLoggerDelegateImpl implements NDocLogger {
    private NDocResource defaultSource;
    private NDocLogger other;
    private int errorCount;

    public NDocLoggerDelegateImpl(NDocResource defaultSource, NDocLogger other) {
        this.defaultSource = defaultSource;
        this.other = other;
    }

    public boolean isSelfSuccessful() {
        return errorCount == 0;
    }

    @Override
    public void log(NDocMsg message) {
        Instant time = Instant.now();
        NMsg msg = message.message();
        Level level = msg.getLevel();
        if (level == null) {
            level = Level.INFO;
        }
        Throwable error = message.error();
        msg = msg.withLevel(level);
        NDocResource source = message.source();
        if (source == null) {
            source = defaultSource;
        }
//        session.out().println(NMsg.ofC("[%s] [%s] [%s] %s", time, type,
//                source == null ? null : source.shortName(),
//                message.getMessage()
//        ));
//        if (error != null) {
//            for (String s : NReservedLangUtils.stacktraceToArray(error)) {
//                session.out().println(NMsg.ofC("\t%s", s));
//            }
//        }
        if (level.intValue() >= Level.SEVERE.intValue()) {
            errorCount++;
        }
        if (other != null) {
            other.log(NDocMsg.of(msg, error, source));
        }
        NLog.of(getClass()).log(msg.getLevel(), NLogVerb.INFO, NMsg.ofC("%s %s",source, msg),error);
    }
}
