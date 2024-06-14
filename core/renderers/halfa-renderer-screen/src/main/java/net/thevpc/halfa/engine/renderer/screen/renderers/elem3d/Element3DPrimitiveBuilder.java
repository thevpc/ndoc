package net.thevpc.halfa.engine.renderer.screen.renderers.elem3d;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;

public interface Element3DPrimitiveBuilder {
    HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState);
}
