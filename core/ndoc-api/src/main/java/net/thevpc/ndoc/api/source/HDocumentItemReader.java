package net.thevpc.ndoc.api.source;

import net.thevpc.ndoc.api.model.node.HNode;

public interface HDocumentItemReader {

    HNode read(HDocumentItemReaderContext context);
}
