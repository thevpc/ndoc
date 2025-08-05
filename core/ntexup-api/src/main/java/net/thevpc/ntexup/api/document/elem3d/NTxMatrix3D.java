package net.thevpc.ntexup.api.document.elem3d;

public class NTxMatrix3D {
    double[][] m;

    public NTxMatrix3D(double[][] m) {
        this.m = m;
    }

    public static NTxMatrix3D identity() {
        return new NTxMatrix3D(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    // Translation matrix
    public NTxMatrix3D translate(double tx, double ty, double tz) {
        return multiply(new NTxMatrix3D(new double[][]{
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}
        }));
    }

    // Rotation matrix around the X axis
    public NTxMatrix3D rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new NTxMatrix3D(
                new double[][]{
                        {1, 0, 0, 0},
                        {0, cos, -sin, 0},
                        {0, sin, cos, 0},
                        {0, 0, 0, 1}
                }
        ));
    }

    // Rotation matrix around the Y axis
    public NTxMatrix3D rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new NTxMatrix3D(
                new double[][]{
                        {cos, 0, sin, 0},
                        {0, 1, 0, 0},
                        {-sin, 0, cos, 0},
                        {0, 0, 0, 1}
                }
        ));
    }

    // Rotation matrix around the Z axis
    public NTxMatrix3D rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new NTxMatrix3D(new double[][]{
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        }));
    }

    // Scaling matrix
    public NTxMatrix3D scale(double sx, double sy, double sz) {
        return multiply(new NTxMatrix3D(new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        }));
    }

    public NTxMatrix3D multiply(NTxMatrix3D b) {
        return multiply(this, b);
    }

    // Multiply two matrices
    public static NTxMatrix3D multiply(NTxMatrix3D a, NTxMatrix3D b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a.m[i][k] * b.m[k][j];
                }
            }
        }
        return new NTxMatrix3D(result);
    }
}
