package net.thevpc.halfa.api.model.elem3d;

import net.thevpc.halfa.api.model.elem2d.HPoint2D;

public class Projection3D {
    double focalLength;

    public Projection3D(double focalLength) {
        this.focalLength = focalLength;
    }

    // Project a 3D point onto a 2D plane
    public HPoint2D project(HPoint3D point) {
        double factor = focalLength / (focalLength + point.z);
        int x = (int) (point.x * factor);
        int y = (int) (point.y * factor);
        return new HPoint2D(x, y);
    }
}
