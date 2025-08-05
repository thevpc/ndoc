package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.log.NTxLogger;
import net.thevpc.ntexup.api.document.NTxDocument;

public interface NTxCompilePageContext {
    NTxDocument document();
    NTxLogger messages();
    NTxEngine engine();
}
