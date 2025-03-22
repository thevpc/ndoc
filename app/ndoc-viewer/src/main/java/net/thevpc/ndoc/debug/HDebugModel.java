package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.node.HNode;

public class HDebugModel {
    private NDocEngine engine;
    private NDocument rawDocument;
    private HNode currentPage;
    private NDocument compiledDocument;
    private HLogger messageList;

    public NDocEngine getEngine() {
        return engine;
    }

    public HDebugModel setEngine(NDocEngine engine) {
        this.engine = engine;
        return this;
    }

    public HNode getCurrentPage() {
        return currentPage;
    }

    public HDebugModel setCurrentPage(HNode currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public HDebugModel setRawDocument(NDocument rawDocument) {
        this.rawDocument = rawDocument;
        return this;
    }

    public HDebugModel setCompiledDocument(NDocument compiledDocument) {
        this.compiledDocument = compiledDocument;
        return this;
    }

    public NDocument getRawDocument() {
        return rawDocument;
    }

    public NDocument getCompiledDocument() {
        return compiledDocument;
    }

    public HLogger messages() {
        return messageList;
    }

    public HDebugModel setMessageList(HLogger messageList) {
        this.messageList = messageList;
        return this;
    }
}
