package net.thevpc.halfa.api.model.elem3d;

public class HVector3D {
    public double x, y, z;

    public HVector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public HVector3D minus(HVector3D b) {
        return new HVector3D(
                x - b.x,
                y - b.y,
                z - b.z
        );
    }


    public double norm() {
        return Math.sqrt(dot(this));
    }

    public HVector3D normalize() {
        double lrcp = 1.0 / Math.sqrt(dot(this));
        return new HVector3D(this.x * lrcp, this.y * lrcp, this.z * lrcp);
    }

    public HVector3D cross(HVector3D o) {
        return new HVector3D(
                this.y * o.z - this.z * o.y
                , this.z * o.x - this.x * o.z
                , this.x * o.y - this.y * o.x
        );
    }

    public HVector3D plus(HVector3D b) {
        return new HVector3D(
                x + b.x,
                y + b.y,
                z + b.z
        );
    }

    public HVector3D plus(double b) {
        return new HVector3D(
                x + b,
                y + b,
                z + b
        );
    }

    public HVector3D minus(double b) {
        return new HVector3D(
                x - b,
                y - b,
                z - b
        );
    }

    public HVector3D mul(double b) {
        return new HVector3D(
                x * b,
                y * b,
                z * b
        );
    }

    public double dot(HVector3D b) {
        return (
                x * b.x +
                        y * b.y +
                        z * b.z
        );
    }
}
