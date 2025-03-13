package net.thevpc.halfa.api.document;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

public class HMsg {
    private NMsg message;
    private Throwable error;
    private HResource source;

    public static HMsg of(NMsg message, Throwable error, HResource source) {
        return new HMsg(message, error, source);
    }
    public static HMsg of(NMsg message, HResource source) {
        return new HMsg(message, null, source);
    }
    public static HMsg of(NMsg message) {
        return new HMsg(message, null, null);
    }

    public HMsg(NMsg message, Throwable error, HResource source) {
        this.message = message;
        this.error = error;
        this.source = source;
    }

    public NMsg message() {
        return message;
    }

    public Throwable error() {
        return error;
    }

    public HResource source() {
        return source;
    }
}
