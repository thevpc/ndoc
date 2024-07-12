package net.thevpc.halfa.api.document;

import net.thevpc.nuts.util.NOptional;

public interface HDocumentLoadingResult {
    Object source();

    NOptional<HDocument> document();

    boolean isSuccessful();

    HDocument get();

}
