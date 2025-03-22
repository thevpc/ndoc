package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.HNode;

public interface NDocNodeFlowControlProcessor {
    HNode[] process(HNode hNode, NDocNodeFlowControlProcessorContext context);
}
