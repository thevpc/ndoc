package net.thevpc.halfa.api.source;

import net.thevpc.halfa.api.model.node.HNode;

public interface HDocumentItemReader {

    HNode read(HDocumentItemReaderContext context);
}
