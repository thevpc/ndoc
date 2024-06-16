package net.thevpc.halfa.engine.renderer.screen.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;

public class HDebugModel {
    private HEngine engine;
    private HDocument rawDocument;
    private HDocument compiledDocument;
    private HMessageList messageList;

    public HEngine getEngine() {
        return engine;
    }

    public HDebugModel setEngine(HEngine engine) {
        this.engine = engine;
        return this;
    }

    public HDebugModel setRawDocument(HDocument rawDocument) {
        this.rawDocument = rawDocument;
        return this;
    }

    public HDebugModel setCompiledDocument(HDocument compiledDocument) {
        this.compiledDocument = compiledDocument;
        return this;
    }

    public HDocument getRawDocument() {
        return rawDocument;
    }

    public HDocument getCompiledDocument() {
        return compiledDocument;
    }

    public HMessageList messages() {
        return messageList;
    }

    public HDebugModel setMessageList(HMessageList messageList) {
        this.messageList = messageList;
        return this;
    }
}
