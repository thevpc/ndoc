package net.thevpc.ndoc.api.model.elem3d;

import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;

public class NDocProjection3D {
    double focalLength;

    public NDocProjection3D(double focalLength) {
        this.focalLength = focalLength;
    }

    // Project a 3D point onto a 2D plane
    public NDocPoint2D project(NDocPoint3D point) {
        double factor = focalLength / (focalLength + point.z);
        int x = (int) (point.x * factor);
        int y = (int) (point.y * factor);
        return new NDocPoint2D(x, y);
    }
}
