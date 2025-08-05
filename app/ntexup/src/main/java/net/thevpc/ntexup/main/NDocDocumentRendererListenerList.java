package net.thevpc.ntexup.main;

import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererListener;
import net.thevpc.ntexup.api.renderer.NDocDocumentStreamRendererConfig;

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
    public void onChangedPage(NTxNode page) {
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
