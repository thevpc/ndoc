package net.thevpc.ndoc.api.document;

import net.thevpc.nuts.util.NOptional;

public interface NDocDocumentLoadingResult {
    Object source();

    NOptional<NDocument> document();

    boolean isSuccessful();

    NDocument get();

}
