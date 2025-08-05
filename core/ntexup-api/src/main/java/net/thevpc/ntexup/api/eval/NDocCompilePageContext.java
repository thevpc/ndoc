package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.NTxDocument;

public interface NDocCompilePageContext {
    NTxDocument document();
    NDocLogger messages();
    NTxEngine engine();
}
