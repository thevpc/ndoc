package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.node.NTxNode;

public class NDocDebugModel {
    private NDocEngine engine;
    private NDocument rawDocument;
    private NTxNode currentPage;
    private NDocument compiledDocument;
    private NDocLogger messageList;

    public NDocEngine getEngine() {
        return engine;
    }

    public NDocDebugModel setEngine(NDocEngine engine) {
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

    public NDocDebugModel setRawDocument(NDocument rawDocument) {
        this.rawDocument = rawDocument;
        return this;
    }

    public NDocDebugModel setCompiledDocument(NDocument compiledDocument) {
        this.compiledDocument = compiledDocument;
        return this;
    }

    public NDocument getRawDocument() {
        return rawDocument;
    }

    public NDocument getCompiledDocument() {
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
