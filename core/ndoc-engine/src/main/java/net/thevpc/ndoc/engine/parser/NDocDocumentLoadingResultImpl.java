package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.*;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class NDocDocumentLoadingResultImpl implements NDocDocumentLoadingResult {
    private NDocResource source;
    private NDocument document;
    private boolean successful;

    public NDocDocumentLoadingResultImpl(NDocument document,NDocResource source, boolean successful) {
        this.document = document;
        this.source = source;
        this.successful = successful;
    }

    @Override
    public NDocument get() {
        return document().get();
    }

    @Override
    public NDocResource source() {
        return source;
    }


    @Override
    public NOptional<NDocument> document() {
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
