package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxRenderState3D;

public interface NTxElement3DRenderer {
    Class<? extends NtxElement3D> forType();
    NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, NTxRenderState3D renderState);
}
