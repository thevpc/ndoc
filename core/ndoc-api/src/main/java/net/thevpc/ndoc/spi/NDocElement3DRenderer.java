package net.thevpc.ndoc.spi;

import net.thevpc.ndoc.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.HElement3D;
import net.thevpc.ndoc.api.model.elem3d.RenderState3D;

public interface NDocElement3DRenderer {
    Class<? extends HElement3D> forType();
    HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState);
}
