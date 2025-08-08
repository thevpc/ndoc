package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.engine.NTxCompiledDocument;
import net.thevpc.nuts.io.NPath;

public interface NTxDocumentRenderer {

    void render(NTxCompiledDocument document);

    void render(NTxDocument document);

    void renderPath(NPath path);

    void renderSupplier(NTxDocumentRendererSupplier document);

    void addRendererListener(NTxDocumentRendererListener listener);

}
