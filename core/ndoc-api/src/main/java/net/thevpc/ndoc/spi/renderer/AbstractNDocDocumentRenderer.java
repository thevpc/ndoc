package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.node.HNode;

import java.util.ArrayList;
import java.util.List;

import net.thevpc.nuts.io.NPath;

public abstract class AbstractNDocDocumentRenderer implements NDocDocumentRenderer {

    private List<NDocDocumentRendererListener> eventListeners = new ArrayList<>();
    protected NDocDocumentRendererListener eventListenerDelegate = new NDocDocumentRendererListener() {
        @Override
        public void onChangedCompiledDocument(NDocument compiledDocument) {
            for (NDocDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedCompiledDocument(compiledDocument);
            }
        }

        @Override
        public void onChangedRawDocument(NDocument rawDocument) {
            for (NDocDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedRawDocument(rawDocument);
            }
        }

        @Override
        public void onChangedPage(HNode page) {
            for (NDocDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedPage(page);
            }
        }

        @Override
        public void onCloseView() {
            for (NDocDocumentRendererListener eventListener : eventListeners) {
                eventListener.onCloseView();
            }
        }

        @Override
        public void onSaveDocument(NDocument document , NDocDocumentStreamRendererConfig config) {
            for (NDocDocumentRendererListener eventListener : eventListeners) {
                eventListener.onSaveDocument(document,config);
            }
        }
    };
    protected final NDocEngine engine;
    protected HLogger messages;

    public AbstractNDocDocumentRenderer(NDocEngine engine) {
        this.engine = engine;
    }

    @Override
    public void addRendererListener(NDocDocumentRendererListener listener) {
        if (listener != null) {
            this.eventListeners.add(listener);
        }
    }

    @Override
    public void renderPath(NPath path) {
        renderSupplier(r -> engine.loadDocument(path, r.messages()).get());
    }

    @Override
    public void render(NDocument document) {
        renderSupplier(e -> document);
    }

    @Override
    public HLogger log() {
        return messages;
    }

    @Override
    public NDocDocumentRenderer setLogger(HLogger logger) {
        this.messages = logger;
        return this;
    }


}
