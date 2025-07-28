package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitive;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2D;

public interface NDocElement2DPrimitiveBuilder {
    net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitive[] toPrimitives(net.thevpc.ndoc.api.document.elem2d.NDocElement2D e);
}
