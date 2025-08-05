package net.thevpc.ntexup.api.document;

import net.thevpc.nuts.util.NOptional;

public interface NTxDocumentLoadingResult {
    Object source();

    NOptional<NTxDocument> document();

    boolean isSuccessful();

    NTxDocument get();

}
