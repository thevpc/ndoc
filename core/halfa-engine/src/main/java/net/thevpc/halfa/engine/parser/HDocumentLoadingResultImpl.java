package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.api.document.*;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class HDocumentLoadingResultImpl implements HDocumentLoadingResult {
    private HResource source;
    private HDocument document;
    private HLoggerDelegateImpl messageList;

    public HDocumentLoadingResultImpl(HResource source, HLogger messageList) {
        this.source = source;
        if (messageList == null) {
            messageList = new HLoggerImpl(source);
        }
        this.messageList = new HLoggerDelegateImpl(source, messageList);
    }

    @Override
    public HDocument get() {
        return document().get();
    }

    @Override
    public HResource source() {
        return source;
    }

    public HLoggerDelegateImpl messages() {
        return messageList;
    }

    @Override
    public NOptional<HDocument> document() {
        if (/*isSuccessful() && */document != null) {
            return NOptional.of(document);
        } else {
            if (isSuccessful()) {
                return NOptional.ofEmpty(NMsg.ofC("compilation is successful but document could not be compiled"));
            }
            return NOptional.ofEmpty(NMsg.ofC("Compilation failed and partial document could not resolved"));
        }
    }

    @Override
    public boolean isSuccessful() {
        return messageList.isSelfSuccessful();
    }

    public void setDocument(HDocument document) {
        this.document = document;
    }

}
