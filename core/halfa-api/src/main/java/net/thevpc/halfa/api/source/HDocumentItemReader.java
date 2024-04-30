package net.thevpc.halfa.api.source;

import net.thevpc.halfa.api.model.HDocumentItem;

public interface HDocumentItemReader {

    HDocumentItem read(HDocumentItemReaderContext context);
}
