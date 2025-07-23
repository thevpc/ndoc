package net.thevpc.ndoc.api.eval;


import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocLogger;

public interface NDocFunctionContext {
    NDocLogger messages();
    NDocEngine engine();
}
