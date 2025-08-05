package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;

public interface NDocDocumentRendererListener {

    default void onChangedCompiledDocument(NDocument compiledDocument) {
    }

    default void onChangedRawDocument(NDocument rawDocument) {
    }

    default void onChangedPage(NTxNode page) {
    }

    default void onSaveDocument(NDocument document, NDocDocumentStreamRendererConfig config) {
    }

    default void onCloseView() {
    }

    default void onStartLoadingDocument(){};

    default void onEndLoadingDocument(){};
}
