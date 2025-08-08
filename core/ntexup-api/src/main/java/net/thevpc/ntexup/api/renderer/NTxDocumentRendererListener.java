package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.engine.NTxCompiledDocument;
import net.thevpc.ntexup.api.engine.NTxCompiledPage;

public interface NTxDocumentRendererListener {

    default void onChangedCompiledDocument(NTxCompiledDocument compiledDocument) {
    }

    default void onChangedPage(NTxCompiledPage page) {
    }

    default void onSaveDocument(NTxCompiledDocument document, NTxDocumentStreamRendererConfig config) {
    }

    default void onCloseView() {
    }

    default void onStartLoadingDocument(){};

    default void onEndLoadingDocument(){};
}
