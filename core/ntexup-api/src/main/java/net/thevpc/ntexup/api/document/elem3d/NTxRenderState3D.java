package net.thevpc.ntexup.api.document.elem3d;

public interface NTxRenderState3D {
    NTxVector3D lightOrientation();

    NtxElement3DPrimitive[] toPrimitives(NtxElement3D e);
}
