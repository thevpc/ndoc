package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;

public interface NTxDocumentRendererListener {

    default void onChangedCompiledDocument(NTxDocument compiledDocument) {
    }

    default void onChangedRawDocument(NTxDocument rawDocument) {
    }

    default void onChangedPage(NTxNode page) {
    }

    default void onSaveDocument(NTxDocument document, NTxDocumentStreamRendererConfig config) {
    }

    default void onCloseView() {
    }

    default void onStartLoadingDocument(){};

    default void onEndLoadingDocument(){};
}
