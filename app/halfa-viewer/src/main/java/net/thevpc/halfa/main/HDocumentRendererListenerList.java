package net.thevpc.halfa.main;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;

import java.util.ArrayList;
import java.util.List;

public class HDocumentRendererListenerList implements HDocumentRendererListener {
    private List<HDocumentRendererListener> registeredHDocumentRendererListener = new ArrayList<>();

    public void add(HDocumentRendererListener a) {
        if (a != null) {
            registeredHDocumentRendererListener.add(a);
        }
    }

    @Override
    public void onChangedCompiledDocument(HDocument compiledDocument) {
        for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedCompiledDocument(compiledDocument);
        }
    }

    @Override
    public void onChangedRawDocument(HDocument rawDocument) {
        for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedRawDocument(rawDocument);
        }
    }

    @Override
    public void onChangedPage(HNode page) {
        for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedPage(page);
        }
    }

    @Override
    public void onCloseView() {
        for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onCloseView();
        }
    }

    @Override
    public void onSaveDocument(HDocument document, HDocumentStreamRendererConfig config) {
        for (HDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onSaveDocument(document, config);
        }
    }

    @Override
    public void onStartLoadingDocument() {
        for (HDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onStartLoadingDocument();
        }
    }

    @Override
    public void onEndLoadingDocument() {
        for (HDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onEndLoadingDocument();
        }
    }

    public void remove(HDocumentRendererListener a) {
        registeredHDocumentRendererListener.remove(a);
    }
}
