package net.thevpc.ntexup.engine.parser;

import net.thevpc.ntexup.api.document.*;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class NTxDocumentLoadingResultImpl implements NTxDocumentLoadingResult {
    private NDocResource source;
    private NTxDocument document;
    private boolean successful;

    public NTxDocumentLoadingResultImpl(NTxDocument document, NDocResource source, boolean successful) {
        this.document = document;
        this.source = source;
        this.successful = successful;
    }

    @Override
    public NTxDocument get() {
        return document().get();
    }

    @Override
    public NDocResource source() {
        return source;
    }


    @Override
    public NOptional<NTxDocument> document() {
        if (document == null) {
            if(successful) {
                return NOptional.ofEmpty(NMsg.ofC("compilation is successful but document could not be compiled"));

            }
            return NOptional.ofEmpty(NMsg.ofC("Compilation failed and partial document could not resolved"));
        }
        return NOptional.of(document);
    }

    @Override
    public boolean isSuccessful() {
        return successful;
        //return messageList.isSelfSuccessful();
    }

}
