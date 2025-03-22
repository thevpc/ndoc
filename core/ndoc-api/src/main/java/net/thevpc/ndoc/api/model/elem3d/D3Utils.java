package net.thevpc.ndoc.api.model.elem3d;

public class D3Utils {

    public static HVector3D surfaceNormal(HPoint3D p1, HPoint3D p2, HPoint3D p3) {
        return surfaceNormal(p1.asVector(), p2.asVector(), p3.asVector());
    }

    public static HVector3D surfaceNormal(HVector3D p1, HVector3D p2, HVector3D p3) {
        return ((p2.minus(p1)).cross(p3.minus(p1))).normalize();
    }
}
