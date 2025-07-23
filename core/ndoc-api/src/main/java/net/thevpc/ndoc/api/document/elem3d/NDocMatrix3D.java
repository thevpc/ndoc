package net.thevpc.ndoc.api.document.elem3d;

public class NDocMatrix3D {
    double[][] m;

    public NDocMatrix3D(double[][] m) {
        this.m = m;
    }

    public static NDocMatrix3D identity() {
        return new NDocMatrix3D(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    // Translation matrix
    public NDocMatrix3D translate(double tx, double ty, double tz) {
        return multiply(new NDocMatrix3D(new double[][]{
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}
        }));
    }

    // Rotation matrix around the X axis
    public NDocMatrix3D rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new NDocMatrix3D(
                new double[][]{
                        {1, 0, 0, 0},
                        {0, cos, -sin, 0},
                        {0, sin, cos, 0},
                        {0, 0, 0, 1}
                }
        ));
    }

    // Rotation matrix around the Y axis
    public NDocMatrix3D rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new NDocMatrix3D(
                new double[][]{
                        {cos, 0, sin, 0},
                        {0, 1, 0, 0},
                        {-sin, 0, cos, 0},
                        {0, 0, 0, 1}
                }
        ));
    }

    // Rotation matrix around the Z axis
    public NDocMatrix3D rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new NDocMatrix3D(new double[][]{
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        }));
    }

    // Scaling matrix
    public NDocMatrix3D scale(double sx, double sy, double sz) {
        return multiply(new NDocMatrix3D(new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        }));
    }

    public NDocMatrix3D multiply(NDocMatrix3D b) {
        return multiply(this, b);
    }

    // Multiply two matrices
    public static NDocMatrix3D multiply(NDocMatrix3D a, NDocMatrix3D b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a.m[i][k] * b.m[k][j];
                }
            }
        }
        return new NDocMatrix3D(result);
    }
}
