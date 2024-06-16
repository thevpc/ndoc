package net.thevpc.halfa.api.document;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import java.time.Instant;

public class HDocumentLoadingMessage {
    private int id;
    private HMessageType type;
    private Instant time;
    private NMsg message;
    private HResource source;
    private Throwable error;

    public HDocumentLoadingMessage(int id, HMessageType type, Instant time, NMsg message, HResource source, Throwable error) {
        this.id = id;
        this.type = type;
        this.time = time;
        this.message = message;
        this.source = source;
        this.error = error;
    }

    public int getId() {
        return id;
    }

    public HMessageType getType() {
        return type;
    }

    public Instant getTime() {
        return time;
    }

    public NMsg getMessage() {
        return message;
    }

    public HResource getSource() {
        return source;
    }

    public Throwable getError() {
        return error;
    }
}
