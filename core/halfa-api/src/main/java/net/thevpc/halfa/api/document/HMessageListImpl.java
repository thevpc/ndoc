package net.thevpc.halfa.api.document;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.reserved.NReservedLangUtils;
import net.thevpc.nuts.util.NMsg;

import java.time.Instant;

public class HMessageListImpl implements HMessageList {
    private NSession session;
    private HResource defaultSource;

    public HMessageListImpl(NSession session, HResource defaultSource) {
        this.session = session;
        this.defaultSource = defaultSource;
    }

    @Override
    public void addMessage(HMessageType type, NMsg message, Throwable error, HResource source) {
        Instant time = Instant.now();
        if (type == null) {
            type = HMessageType.INFO;
        }
        if (source == null) {
            source = defaultSource;
        }
        session.out().println(NMsg.ofC("[%s] [%s] [%s] %s", time, type,
                source == null ? null : source.shortName(),
                message.getMessage()
        ));
        if (error != null) {
            for (String s : NReservedLangUtils.stacktraceToArray(error)) {
                session.out().println(NMsg.ofC("\t%s", s));
            }
        }
    }
}
