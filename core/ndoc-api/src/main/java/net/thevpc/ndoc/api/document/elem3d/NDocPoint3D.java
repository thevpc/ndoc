package net.thevpc.ndoc.api.document.elem3d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

import java.util.Objects;

public class NDocPoint3D implements NToElement {
    public double x, y, z;

    public NDocPoint3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public NDocVector3D asVector() {
        return new NDocVector3D(x, y, z);
    }

    public NDocPoint3D minus(NDocPoint3D b) {
        return new NDocPoint3D(
                x - b.x,
                y - b.y,
                z - b.z
        );
    }


    public double length() {
        return Math.sqrt(dot(this));
    }

    public NDocPoint3D plus(NDocPoint3D b) {
        return new NDocPoint3D(
                x + b.x,
                y + b.y,
                z + b.z
        );
    }

    public NDocPoint3D plus(double b) {
        return new NDocPoint3D(
                x + b,
                y + b,
                z + b
        );
    }

    public NDocPoint3D minus(double b) {
        return new NDocPoint3D(
                x - b,
                y - b,
                z - b
        );
    }

    public NDocPoint3D mul(double b) {
        return new NDocPoint3D(
                x * b,
                y * b,
                z * b
        );
    }

    public double dot(NDocPoint3D b) {
        return (
                x * b.x +
                        y * b.y +
                        z * b.z
        );
    }

    // Apply a transformation matrix to the point
    public NDocPoint3D transform(double[][] matrix) {
        double newX = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z + matrix[0][3];
        double newY = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z + matrix[1][3];
        double newZ = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z + matrix[2][3];
        return new NDocPoint3D(newX, newY, newZ);
    }

    // Apply a transformation matrix to the point
    public NDocPoint3D transform(NDocMatrix3D transform) {
        double[][] m = transform.m;
        double newX = m[0][0] * x + m[0][1] * y + m[0][2] * z + m[0][3];
        double newY = m[1][0] * x + m[1][1] * y + m[1][2] * z + m[1][3];
        double newZ = m[2][0] * x + m[2][1] * y + m[2][2] * z + m[2][3];
        return new NDocPoint3D(newX, newY, newZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NDocPoint3D point3D = (NDocPoint3D) o;
        return Double.compare(x, point3D.x) == 0 && Double.compare(y, point3D.y) == 0 && Double.compare(z, point3D.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public NElement toElement() {
        return NElement.ofUplet(
                NElement.ofDouble(getX()),
                NElement.ofDouble(getY()),
                NElement.ofDouble(getZ())
        );
    }
}
