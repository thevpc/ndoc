package net.thevpc.halfa.api.document;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import java.time.Instant;

public class HMessageListDelegateImpl implements HMessageList {
    private HResource defaultSource;
    private HMessageList other;
    private int errorCount;

    public HMessageListDelegateImpl(HResource defaultSource, HMessageList other) {
        this.defaultSource = defaultSource;
        this.other = other;
    }

    public boolean isSelfSuccessful() {
        return errorCount == 0;
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
//        session.out().println(NMsg.ofC("[%s] [%s] [%s] %s", time, type,
//                source == null ? null : source.shortName(),
//                message.getMessage()
//        ));
//        if (error != null) {
//            for (String s : NReservedLangUtils.stacktraceToArray(error)) {
//                session.out().println(NMsg.ofC("\t%s", s));
//            }
//        }
        if (type == HMessageType.ERROR) {
            errorCount++;
        }
        if (other != null) {
            other.addMessage(type, message, error, source);
        }
    }
}
