package net.thevpc.ndoc.spi.nodes;

import net.thevpc.ndoc.api.model.elem2d.NDocElement2DPrimitive;
import net.thevpc.ndoc.api.model.elem2d.NDocElement2D;

public interface NDocElement2DPrimitiveBuilder {
    NDocElement2DPrimitive[] toPrimitives(NDocElement2D e);
}
