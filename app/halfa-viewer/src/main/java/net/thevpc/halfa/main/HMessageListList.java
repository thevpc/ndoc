package net.thevpc.halfa.main;

import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.halfa.api.document.HMsg;

import java.util.ArrayList;
import java.util.List;

public class HMessageListList implements HLogger {
    private List<HLogger> registeredMessages = new ArrayList<>();

    @Override
    public void log(HMsg msg) {
        for (HLogger r : registeredMessages) {
            r.log(msg);
        }
    }

    public void add(HLogger a) {
        if (a != null) {
            registeredMessages.add(a);
        }
    }

    public void remove(HLogger a) {
        if (a != null) {
            registeredMessages.remove(a);
        }
    }
}
