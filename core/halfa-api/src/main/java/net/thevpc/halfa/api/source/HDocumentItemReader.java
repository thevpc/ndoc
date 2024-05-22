package net.thevpc.halfa.api.source;

import net.thevpc.halfa.api.node.HNode;

public interface HDocumentItemReader {

    HNode read(HDocumentItemReaderContext context);
}
