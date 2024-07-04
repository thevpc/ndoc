package net.thevpc.halfa.spi;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;

public interface HElement3DRenderer {
    Class<? extends HElement3D> forType();
    HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState);
}
