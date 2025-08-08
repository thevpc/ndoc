package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.engine.NTxCompiledDocument;
import net.thevpc.ntexup.api.engine.NTxCompiledPage;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.NTxDocument;

import java.util.ArrayList;
import java.util.List;

import net.thevpc.nuts.io.NPath;

public abstract class NTxDocumentRendererBase implements NTxDocumentRenderer {

    private List<NTxDocumentRendererListener> eventListeners = new ArrayList<>();
    protected NTxDocumentRendererListener eventListenerDelegate = new NTxDocumentRendererListener() {
        @Override
        public void onChangedCompiledDocument(NTxCompiledDocument compiledDocument) {
            for (NTxDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedCompiledDocument(compiledDocument);
            }
        }

        @Override
        public void onChangedPage(NTxCompiledPage page) {
            for (NTxDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedPage(page);
            }
        }

        @Override
        public void onCloseView() {
            for (NTxDocumentRendererListener eventListener : eventListeners) {
                eventListener.onCloseView();
            }
        }

        @Override
        public void onSaveDocument(NTxCompiledDocument document, NTxDocumentStreamRendererConfig config) {
            for (NTxDocumentRendererListener eventListener : eventListeners) {
                eventListener.onSaveDocument(document, config);
            }
        }
    };
    protected final NTxEngine engine;

    public NTxDocumentRendererBase(NTxEngine engine) {
        this.engine = engine;
    }

    @Override
    public void addRendererListener(NTxDocumentRendererListener listener) {
        if (listener != null) {
            this.eventListeners.add(listener);
        }
    }

    @Override
    public void renderPath(NPath path) {
        renderSupplier(r -> engine.loadCompiledDocument(path));
    }

    @Override
    public void render(NTxDocument document) {
        renderSupplier(e -> engine.asCompiledDocument(document));
    }

    @Override
    public void render(NTxCompiledDocument document) {
        renderSupplier(e -> document);
    }

}
