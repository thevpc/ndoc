package net.thevpc.ndoc.api.source;

import net.thevpc.ndoc.api.model.node.NDocNode;

public interface HDocumentItemReader {

    NDocNode read(HDocumentItemReaderContext context);
}
