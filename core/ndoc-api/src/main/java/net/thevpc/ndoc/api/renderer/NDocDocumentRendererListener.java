package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.node.NDocNode;

public interface NDocDocumentRendererListener {

    default void onChangedCompiledDocument(NDocument compiledDocument) {
    }

    default void onChangedRawDocument(NDocument rawDocument) {
    }

    default void onChangedPage(NDocNode page) {
    }

    default void onSaveDocument(NDocument document, NDocDocumentStreamRendererConfig config) {
    }

    default void onCloseView() {
    }

    default void onStartLoadingDocument(){};

    default void onEndLoadingDocument(){};
}
