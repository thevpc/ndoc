package net.thevpc.ndoc.api.document;

import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.nuts.util.NMsg;

public class NDocMsg {
    private NMsg message;
    private Throwable error;
    private NDocResource source;

    public static NDocMsg of(NMsg message, Throwable error, NDocResource source) {
        return new NDocMsg(message, error, source);
    }
    public static NDocMsg of(NMsg message, NDocResource source) {
        return new NDocMsg(message, null, source);
    }
    public static NDocMsg of(NMsg message) {
        return new NDocMsg(message, null, null);
    }

    public NDocMsg(NMsg message, Throwable error, NDocResource source) {
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

    public NDocResource source() {
        return source;
    }
}
