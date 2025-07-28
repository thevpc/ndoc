package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.elem3d.NDocElement3DPrimitive;
import net.thevpc.ndoc.api.document.elem3d.NDocElement3D;
import net.thevpc.ndoc.api.document.elem3d.RenderState3D;

public interface NDocElement3DRenderer {
    Class<? extends NDocElement3D> forType();
    NDocElement3DPrimitive[] toPrimitives(NDocElement3D e, RenderState3D renderState);
}
