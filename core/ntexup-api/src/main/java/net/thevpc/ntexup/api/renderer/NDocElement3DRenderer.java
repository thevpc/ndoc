package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.RenderState3D;

public interface NDocElement3DRenderer {
    Class<? extends NtxElement3D> forType();
    NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, RenderState3D renderState);
}
