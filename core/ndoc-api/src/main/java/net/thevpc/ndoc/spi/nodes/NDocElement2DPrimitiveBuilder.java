package net.thevpc.ndoc.spi.nodes;

import net.thevpc.ndoc.api.model.elem2d.Element2DPrimitive;
import net.thevpc.ndoc.api.model.elem2d.HElement2D;

public interface NDocElement2DPrimitiveBuilder {
    Element2DPrimitive[] toPrimitives(HElement2D e);
}
