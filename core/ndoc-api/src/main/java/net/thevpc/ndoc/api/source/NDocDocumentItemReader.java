package net.thevpc.ndoc.api.source;

import net.thevpc.ndoc.api.model.node.NDocNode;

public interface NDocDocumentItemReader {

    NDocNode read(NDocDocumentItemReaderContext context);
}
