package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.api.document.*;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class HDocumentLoadingResultImpl implements HDocumentLoadingResult {
    private HResource source;
    private HDocument document;
    private NSession session;
    private HMessageListDelegateImpl messageList;

    public HDocumentLoadingResultImpl(HResource source, HMessageList messageList, NSession session) {
        this.source = source;
        this.session = session;
        if (messageList == null) {
            messageList = new HMessageListImpl(session, source);
        }
        this.messageList = new HMessageListDelegateImpl(session, source, messageList);
    }

    @Override
    public HDocument get() {
        return document().get(session);
    }

    @Override
    public HResource source() {
        return source;
    }

    public HMessageListDelegateImpl messages() {
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
