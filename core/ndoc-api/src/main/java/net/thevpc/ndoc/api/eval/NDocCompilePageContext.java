package net.thevpc.ndoc.api.eval;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.document.NDocument;

public interface NDocCompilePageContext {
    NDocument document();
    NDocLogger messages();
    NDocEngine engine();
}
