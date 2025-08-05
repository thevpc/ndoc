package net.thevpc.ntexup.api.document.elem3d;

public class NTxD3Utils {

    public static NTxVector3D surfaceNormal(NTxPoint3D p1, NTxPoint3D p2, NTxPoint3D p3) {
        return surfaceNormal(p1.asVector(), p2.asVector(), p3.asVector());
    }

    public static NTxVector3D surfaceNormal(NTxVector3D p1, NTxVector3D p2, NTxVector3D p3) {
        return ((p2.minus(p1)).cross(p3.minus(p1))).normalize();
    }
}
