package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.document.node.NDocNode;

public class NDocDebugModel {
    private NDocEngine engine;
    private NDocument rawDocument;
    private NDocNode currentPage;
    private NDocument compiledDocument;
    private NDocLogger messageList;

    public NDocEngine getEngine() {
        return engine;
    }

    public NDocDebugModel setEngine(NDocEngine engine) {
        this.engine = engine;
        return this;
    }

    public NDocNode getCurrentPage() {
        return currentPage;
    }

    public NDocDebugModel setCurrentPage(NDocNode currentPage) {
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
