package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.model.node.HNode;

public interface HNodeFlowControlProcessor {
    HNode[] process(HNode hNode,HNodeFlowControlProcessorContext context);
}
