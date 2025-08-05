package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.node.NTxNode;

public class NDocDebugModel {
    private NTxEngine engine;
    private NTxDocument rawDocument;
    private NTxNode currentPage;
    private NTxDocument compiledDocument;
    private NDocLogger messageList;

    public NTxEngine getEngine() {
        return engine;
    }

    public NDocDebugModel setEngine(NTxEngine engine) {
        this.engine = engine;
        return this;
    }

    public NTxNode getCurrentPage() {
        return currentPage;
    }

    public NDocDebugModel setCurrentPage(NTxNode currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public NDocDebugModel setRawDocument(NTxDocument rawDocument) {
        this.rawDocument = rawDocument;
        return this;
    }

    public NDocDebugModel setCompiledDocument(NTxDocument compiledDocument) {
        this.compiledDocument = compiledDocument;
        return this;
    }

    public NTxDocument getRawDocument() {
        return rawDocument;
    }

    public NTxDocument getCompiledDocument() {
        return compiledDocument;
    }

    public NDocLogger messages() {
        return messageList;
    }

    public NDocDebugModel setMessageList(NDocLogger messageList) {
        this.messageList = messageList;
        return this;
    }
}
