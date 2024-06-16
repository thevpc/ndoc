package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HDocument;

public interface HDocumentRendererListener {

    void onChangedCompiledDocument(HDocument compiledDocument);

    void onChangedRawDocument(HDocument rawDocument);
}
