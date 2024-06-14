package net.thevpc.halfa.api.model.elem3d;

public class Matrix3D {
    double[][] m;

    public Matrix3D(double[][] m) {
        this.m = m;
    }

    public static Matrix3D identity() {
        return new Matrix3D(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    // Translation matrix
    public Matrix3D translate(double tx, double ty, double tz) {
        return multiply(new Matrix3D(new double[][]{
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}
        }));
    }

    // Rotation matrix around the X axis
    public Matrix3D rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new Matrix3D(
                new double[][]{
                        {1, 0, 0, 0},
                        {0, cos, -sin, 0},
                        {0, sin, cos, 0},
                        {0, 0, 0, 1}
                }
        ));
    }

    // Rotation matrix around the Y axis
    public Matrix3D rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new Matrix3D(
                new double[][]{
                        {cos, 0, sin, 0},
                        {0, 1, 0, 0},
                        {-sin, 0, cos, 0},
                        {0, 0, 0, 1}
                }
        ));
    }

    // Rotation matrix around the Z axis
    public Matrix3D rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return multiply(new Matrix3D(new double[][]{
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        }));
    }

    // Scaling matrix
    public Matrix3D scale(double sx, double sy, double sz) {
        return multiply(new Matrix3D(new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        }));
    }

    public Matrix3D multiply(Matrix3D b) {
        return multiply(this, b);
    }

    // Multiply two matrices
    public static Matrix3D multiply(Matrix3D a, Matrix3D b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a.m[i][k] * b.m[k][j];
                }
            }
        }
        return new Matrix3D(result);
    }
}
