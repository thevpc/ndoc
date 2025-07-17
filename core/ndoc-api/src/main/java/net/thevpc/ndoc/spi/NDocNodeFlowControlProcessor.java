package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.NDocNode;

public interface NDocNodeFlowControlProcessor {
    NDocNode[] process(NDocNode node, NDocNodeFlowControlProcessorContext context);
}
