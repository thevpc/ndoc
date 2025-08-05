package net.thevpc.ntexup.api.document.elem3d;

public interface RenderState3D {
    NTxVector3D lightOrientation();

    NtxElement3DPrimitive[] toPrimitives(NtxElement3D e);
}
