package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.engine.DefaultNDocLogger;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.engine.NDocLoggerDelegateImpl;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class NDocDocumentLoadingResultImpl implements NDocDocumentLoadingResult {
    private NDocResource source;
    private NDocument document;
    private NDocLoggerDelegateImpl messageList;

    public NDocDocumentLoadingResultImpl(NDocResource source, NDocLogger messageList) {
        this.source = source;
        if (messageList == null) {
            messageList = new DefaultNDocLogger(source);
        }
        this.messageList = new NDocLoggerDelegateImpl(source, messageList);
    }

    @Override
    public NDocument get() {
        return document().get();
    }

    @Override
    public NDocResource source() {
        return source;
    }

    public NDocLoggerDelegateImpl messages() {
        return messageList;
    }

    @Override
    public NOptional<NDocument> document() {
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

    public void setDocument(NDocument document) {
        this.document = document;
    }

}
