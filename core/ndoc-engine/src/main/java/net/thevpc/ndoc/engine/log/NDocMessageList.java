package net.thevpc.ndoc.engine.log;

import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.log.NDocMsg;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.util.NMsg;

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

    @Override
    public void log(NMsg message) {
        for (NDocLogger r : registeredMessages) {
            r.log(message);
        }
    }

    @Override
    public void log(NMsg message, NDocResource source) {
        for (NDocLogger r : registeredMessages) {
            r.log(message,source);
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
