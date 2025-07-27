package net.thevpc.ndoc.main;

import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.NDocMsg;

import java.util.ArrayList;
import java.util.List;

public class NDocMessageList implements NDocLogger {
    private List<NDocLogger> registeredMessages = new ArrayList<>();

    @Override
    public void log(NDocMsg msg) {
        for (NDocLogger r : registeredMessages) {
            r.log(msg);
        }
    }

    public void add(NDocLogger a) {
        if (a != null) {
            registeredMessages.add(a);
        }
    }

    public void remove(NDocLogger a) {
        if (a != null) {
            registeredMessages.remove(a);
        }
    }
}
