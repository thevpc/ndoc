package net.thevpc.ndoc.api.document.elem3d;

public interface RenderState3D {
    NDocVector3D lightOrientation();

    NDocElement3DPrimitive[] toPrimitives(NDocElement3D e);
}
