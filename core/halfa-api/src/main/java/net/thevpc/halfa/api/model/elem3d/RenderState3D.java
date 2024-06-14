package net.thevpc.halfa.api.model.elem3d;

public interface RenderState3D {
    HVector3D lightOrientation();

    HElement3DPrimitive[] toPrimitives(HElement3D e);
}
