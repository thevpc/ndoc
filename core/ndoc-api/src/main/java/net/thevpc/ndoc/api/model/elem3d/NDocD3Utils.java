package net.thevpc.ndoc.api.model.elem3d;

public class NDocD3Utils {

    public static NDocVector3D surfaceNormal(NDocPoint3D p1, NDocPoint3D p2, NDocPoint3D p3) {
        return surfaceNormal(p1.asVector(), p2.asVector(), p3.asVector());
    }

    public static NDocVector3D surfaceNormal(NDocVector3D p1, NDocVector3D p2, NDocVector3D p3) {
        return ((p2.minus(p1)).cross(p3.minus(p1))).normalize();
    }
}
