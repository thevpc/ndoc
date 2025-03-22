package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.nuts.io.NPath;

public interface NDocDocumentRenderer {

    void render(NDocument document);

    void renderPath(NPath path);

    void renderSupplier(NDocDocumentRendererSupplier document);

    void addRendererListener(NDocDocumentRendererListener listener);

    HLogger log();

    NDocDocumentRenderer setLogger(HLogger logger);
}
