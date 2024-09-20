package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.node.HNode;

public interface HDocumentRendererListener {

    default void onChangedCompiledDocument(HDocument compiledDocument) {
    }

    default void onChangedRawDocument(HDocument rawDocument) {
    }

    default void onChangedPage(HNode page) {
    }

    default void onSaveDocument(HDocument document, HDocumentStreamRendererConfig config) {
    }

    default void onCloseView() {
    }

    default void onStartLoadingDocument(){};

    default void onEndLoadingDocument(){};
}
