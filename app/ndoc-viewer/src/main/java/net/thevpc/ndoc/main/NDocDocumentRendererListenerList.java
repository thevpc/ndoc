package net.thevpc.ndoc.main;

import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocDocumentRendererListener;
import net.thevpc.ndoc.spi.renderer.NDocDocumentStreamRendererConfig;

import java.util.ArrayList;
import java.util.List;

public class NDocDocumentRendererListenerList implements NDocDocumentRendererListener {
    private List<NDocDocumentRendererListener> registeredHDocumentRendererListener = new ArrayList<>();

    public void add(NDocDocumentRendererListener a) {
        if (a != null) {
            registeredHDocumentRendererListener.add(a);
        }
    }

    @Override
    public void onChangedCompiledDocument(NDocument compiledDocument) {
        for (NDocDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedCompiledDocument(compiledDocument);
        }
    }

    @Override
    public void onChangedRawDocument(NDocument rawDocument) {
        for (NDocDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedRawDocument(rawDocument);
        }
    }

    @Override
    public void onChangedPage(HNode page) {
        for (NDocDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedPage(page);
        }
    }

    @Override
    public void onCloseView() {
        for (NDocDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onCloseView();
        }
    }

    @Override
    public void onSaveDocument(NDocument document, NDocDocumentStreamRendererConfig config) {
        for (NDocDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onSaveDocument(document, config);
        }
    }

    @Override
    public void onStartLoadingDocument() {
        for (NDocDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onStartLoadingDocument();
        }
    }

    @Override
    public void onEndLoadingDocument() {
        for (NDocDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onEndLoadingDocument();
        }
    }

    public void remove(NDocDocumentRendererListener a) {
        registeredHDocumentRendererListener.remove(a);
    }
}
