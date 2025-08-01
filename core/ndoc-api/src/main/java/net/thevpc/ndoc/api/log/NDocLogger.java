package net.thevpc.ndoc.api.log;


import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.util.NMsg;

public interface NDocLogger {

    void log(NDocMsg message);
    void log(NMsg message);
    void log(NMsg message, NDocResource source);
}
