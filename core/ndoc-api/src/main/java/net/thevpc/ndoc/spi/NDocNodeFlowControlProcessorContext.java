package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.tson.TsonElement;

public interface NDocNodeFlowControlProcessorContext {
    TsonElement evalExpression(HNode node, TsonElement expression);

    TsonElement resolveVarValue(HNode node, String varName);
}
