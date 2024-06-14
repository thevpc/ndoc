package net.thevpc.halfa.engine.renderer.screen.renderers.elem2d;

import net.thevpc.halfa.api.model.elem2d.Element2DPrimitive;
import net.thevpc.halfa.api.model.elem2d.HElement2D;

public interface Element2DPrimitiveBuilder {
    Element2DPrimitive[] toPrimitives(HElement2D e);
}
