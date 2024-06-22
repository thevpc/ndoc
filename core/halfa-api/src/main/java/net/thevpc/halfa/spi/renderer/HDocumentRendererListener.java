package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HNode;

public interface HDocumentRendererListener {

    void onChangedCompiledDocument(HDocument compiledDocument);

    void onChangedRawDocument(HDocument rawDocument);

    void onChangedPage(HNode page);
    void onCloseView();
}
