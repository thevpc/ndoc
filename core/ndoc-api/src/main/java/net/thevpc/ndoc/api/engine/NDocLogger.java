package net.thevpc.ndoc.api.engine;


import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.util.NMsg;

public interface NDocLogger {

    void log(NDocMsg message);
    void log(NMsg message);
    void log(NMsg message, NDocResource source);
}
