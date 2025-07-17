package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeFlowControlProcessorContext {
    NElement evalExpression(NDocNode node, NElement expression);

    NElement resolveVarValue(NDocNode node, String varName);
}
