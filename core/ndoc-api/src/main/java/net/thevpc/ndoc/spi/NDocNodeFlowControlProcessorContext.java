package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeFlowControlProcessorContext {
    NElement evalExpression(HNode node, NElement expression);

    NElement resolveVarValue(HNode node, String varName);
}
