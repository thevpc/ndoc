package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.tson.TsonElement;

public interface HNodeFlowControlProcessorContext {
    TsonElement evalExpression(HNode node, TsonElement expression);

    TsonElement resolveVarValue(HNode node, String varName);
}
