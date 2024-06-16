package net.thevpc.halfa.api.document;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;


public interface HMessageList {
    default void addError(NMsg message) {
        addMessage(HMessageType.ERROR, message, null, null);
    }

    default void addError(NMsg message, HResource source) {
        addMessage(HMessageType.ERROR, message, null, source);
    }

    default void addError(NMsg message, Throwable throwable, HResource source) {
        addMessage(HMessageType.ERROR, message, throwable, source);
    }

    default void addWarning(NMsg message, HResource source) {
        addMessage(HMessageType.ERROR, message, null, source);
    }

    void addMessage(HMessageType type, NMsg message, Throwable error, HResource source);
}
