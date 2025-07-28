package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.nuts.io.NPath;

public interface NDocDocumentRenderer {

    void render(NDocument document);

    void renderPath(NPath path);

    void renderSupplier(NDocDocumentRendererSupplier document);

    void addRendererListener(NDocDocumentRendererListener listener);

}
