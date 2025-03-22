package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.model.node.HNode;

public interface NDocDocumentRendererListener {

    default void onChangedCompiledDocument(NDocument compiledDocument) {
    }

    default void onChangedRawDocument(NDocument rawDocument) {
    }

    default void onChangedPage(HNode page) {
    }

    default void onSaveDocument(NDocument document, NDocDocumentStreamRendererConfig config) {
    }

    default void onCloseView() {
    }

    default void onStartLoadingDocument(){};

    default void onEndLoadingDocument(){};
}
