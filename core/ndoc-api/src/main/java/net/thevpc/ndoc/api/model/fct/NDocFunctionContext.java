package net.thevpc.ndoc.api.model.fct;


import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocLogger;

public interface NDocFunctionContext {
    NDocLogger messages();
    NDocEngine engine();
}
