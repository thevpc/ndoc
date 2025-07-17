package net.thevpc.ndoc.api.model.elem3d;

public class NDocVector3D {
    public double x, y, z;

    public NDocVector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public NDocVector3D minus(NDocVector3D b) {
        return new NDocVector3D(
                x - b.x,
                y - b.y,
                z - b.z
        );
    }


    public double norm() {
        return Math.sqrt(dot(this));
    }

    public NDocVector3D normalize() {
        double lrcp = 1.0 / Math.sqrt(dot(this));
        return new NDocVector3D(this.x * lrcp, this.y * lrcp, this.z * lrcp);
    }

    public NDocVector3D cross(NDocVector3D o) {
        return new NDocVector3D(
                this.y * o.z - this.z * o.y
                , this.z * o.x - this.x * o.z
                , this.x * o.y - this.y * o.x
        );
    }

    public NDocVector3D plus(NDocVector3D b) {
        return new NDocVector3D(
                x + b.x,
                y + b.y,
                z + b.z
        );
    }

    public NDocVector3D plus(double b) {
        return new NDocVector3D(
                x + b,
                y + b,
                z + b
        );
    }

    public NDocVector3D minus(double b) {
        return new NDocVector3D(
                x - b,
                y - b,
                z - b
        );
    }

    public NDocVector3D mul(double b) {
        return new NDocVector3D(
                x * b,
                y * b,
                z * b
        );
    }

    public double dot(NDocVector3D b) {
        return (
                x * b.x +
                        y * b.y +
                        z * b.z
        );
    }
}
