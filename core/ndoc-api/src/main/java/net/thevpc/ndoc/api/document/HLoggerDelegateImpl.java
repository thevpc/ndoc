package net.thevpc.ndoc.api.document;

import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import java.time.Instant;
import java.util.logging.Level;

public class HLoggerDelegateImpl implements HLogger {
    private HResource defaultSource;
    private HLogger other;
    private int errorCount;

    public HLoggerDelegateImpl(HResource defaultSource, HLogger other) {
        this.defaultSource = defaultSource;
        this.other = other;
    }

    public boolean isSelfSuccessful() {
        return errorCount == 0;
    }

    @Override
    public void log(HMsg message) {
        Instant time = Instant.now();
        NMsg msg = message.message();
        Level level = msg.getLevel();
        if (level == null) {
            level = Level.INFO;
        }
        Throwable error = message.error();
        msg = msg.withLevel(level);
        HResource source = message.source();
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
            other.log(HMsg.of(msg, error, source));
        }
    }
}
