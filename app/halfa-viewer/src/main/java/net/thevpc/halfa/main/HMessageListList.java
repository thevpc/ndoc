package net.thevpc.halfa.main;

import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import java.util.ArrayList;
import java.util.List;

public class HMessageListList implements HMessageList {
    private List<HMessageList> registeredMessages = new ArrayList<>();

    @Override
    public void addMessage(HMessageType type, NMsg message, Throwable error, HResource source) {
        for (HMessageList r : registeredMessages) {
            r.addMessage(type, message, error, source);
        }
    }

    public void add(HMessageList a) {
        if (a != null) {
            registeredMessages.add(a);
        }
    }

    public void remove(HMessageList a) {
        if (a != null) {
            registeredMessages.remove(a);
        }
    }
}
