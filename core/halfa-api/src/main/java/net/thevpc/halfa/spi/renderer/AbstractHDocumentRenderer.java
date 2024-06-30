package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.nuts.NSession;

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
    };
    protected final NSession session;
    protected final HEngine engine;
    protected HMessageList messages;

    public AbstractHDocumentRenderer(HEngine engine, NSession session) {
        this.session = session;
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
    public HMessageList getMessages() {
        return messages;
    }

    @Override
    public HDocumentRenderer setMessages(HMessageList messages) {
        this.messages = messages;
        return this;
    }

}
