package net.thevpc.ndoc.api;

import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.document.NDocument;

public interface CompilePageContext {
    NDocument document();
    NDocLogger messages();
}
