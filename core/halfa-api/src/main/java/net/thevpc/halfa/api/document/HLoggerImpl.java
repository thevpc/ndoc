package net.thevpc.halfa.api.document;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.NOut;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import java.time.Instant;
import java.util.logging.Level;

public class HLoggerImpl implements HLogger {
    private HResource defaultSource;

    public HLoggerImpl() {

    }
    public HLoggerImpl(HResource defaultSource) {
        this.defaultSource = defaultSource;
    }

    @Override
    public void log(HMsg msg) {
        Instant time = Instant.now();
        NMsg nmsg=msg.message();
        Level type=nmsg.getLevel();
        Throwable error=msg.error();
        HResource source=msg.source();
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
