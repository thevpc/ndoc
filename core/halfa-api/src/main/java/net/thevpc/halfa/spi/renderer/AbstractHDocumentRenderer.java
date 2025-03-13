package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.halfa.api.model.node.HNode;

import java.util.ArrayList;
import java.util.List;

import net.thevpc.nuts.io.NPath;

public abstract class AbstractHDocumentRenderer implements HDocumentRenderer {

    private List<HDocumentRendererListener> eventListeners = new ArrayList<>();
    protected HDocumentRendererListener eventListenerDelegate = new HDocumentRendererListener() {
        @Override
        public void onChangedCompiledDocument(HDocument compiledDocument) {
            for (HDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedCompiledDocument(compiledDocument);
            }
        }

        @Override
        public void onChangedRawDocument(HDocument rawDocument) {
            for (HDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedRawDocument(rawDocument);
            }
        }

        @Override
        public void onChangedPage(HNode page) {
            for (HDocumentRendererListener eventListener : eventListeners) {
                eventListener.onChangedPage(page);
            }
        }

        @Override
        public void onCloseView() {
            for (HDocumentRendererListener eventListener : eventListeners) {
                eventListener.onCloseView();
            }
        }

        @Override
        public void onSaveDocument(HDocument document ,HDocumentStreamRendererConfig config) {
            for (HDocumentRendererListener eventListener : eventListeners) {
                eventListener.onSaveDocument(document,config);
            }
        }
    };
    protected final HEngine engine;
    protected HLogger messages;

    public AbstractHDocumentRenderer(HEngine engine) {
        this.engine = engine;
    }

    @Override
    public void addRendererListener(HDocumentRendererListener listener) {
        if (listener != null) {
            this.eventListeners.add(listener);
        }
    }

    @Override
    public void renderPath(NPath path) {
        renderSupplier(r -> engine.loadDocument(path, r.messages()).get());
    }

    @Override
    public void render(HDocument document) {
        renderSupplier(e -> document);
    }

    @Override
    public HLogger log() {
        return messages;
    }

    @Override
    public HDocumentRenderer setLogger(HLogger logger) {
        this.messages = logger;
        return this;
    }


}
