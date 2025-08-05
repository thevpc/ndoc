package net.thevpc.ntexup.main;

import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxDocumentRendererListener;
import net.thevpc.ntexup.api.renderer.NTxDocumentStreamRendererConfig;

import java.util.ArrayList;
import java.util.List;

public class NTxDocumentRendererListenerList implements NTxDocumentRendererListener {
    private List<NTxDocumentRendererListener> registeredHDocumentRendererListener = new ArrayList<>();

    public void add(NTxDocumentRendererListener a) {
        if (a != null) {
            registeredHDocumentRendererListener.add(a);
        }
    }

    @Override
    public void onChangedCompiledDocument(NTxDocument compiledDocument) {
        for (NTxDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedCompiledDocument(compiledDocument);
        }
    }

    @Override
    public void onChangedRawDocument(NTxDocument rawDocument) {
        for (NTxDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedRawDocument(rawDocument);
        }
    }

    @Override
    public void onChangedPage(NTxNode page) {
        for (NTxDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onChangedPage(page);
        }
    }

    @Override
    public void onCloseView() {
        for (NTxDocumentRendererListener r : registeredHDocumentRendererListener) {
            r.onCloseView();
        }
    }

    @Override
    public void onSaveDocument(NTxDocument document, NTxDocumentStreamRendererConfig config) {
        for (NTxDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onSaveDocument(document, config);
        }
    }

    @Override
    public void onStartLoadingDocument() {
        for (NTxDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onStartLoadingDocument();
        }
    }

    @Override
    public void onEndLoadingDocument() {
        for (NTxDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
            eventListener.onEndLoadingDocument();
        }
    }

    public void remove(NTxDocumentRendererListener a) {
        registeredHDocumentRendererListener.remove(a);
    }
}
