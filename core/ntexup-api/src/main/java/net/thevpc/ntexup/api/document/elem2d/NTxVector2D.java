package net.thevpc.ntexup.api.document.elem2d;

public class NTxVector2D {
    public double x, y;

    public NTxVector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public NTxVector2D minus(NTxVector2D b) {
        return new NTxVector2D(
                x - b.x,
                y - b.y
        );
    }


    public double norm() {
        return Math.sqrt(dot(this));
    }

    public NTxVector2D normalize() {
        double lrcp = 1.0 / Math.sqrt(dot(this));
        return new NTxVector2D(this.x * lrcp, this.y * lrcp);
    }

    public NTxVector2D plus(NTxVector2D b) {
        return new NTxVector2D(
                x + b.x,
                y + b.y
        );
    }

    public NTxVector2D plus(double b) {
        return new NTxVector2D(
                x + b,
                y + b
        );
    }

    public NTxVector2D minus(double b) {
        return new NTxVector2D(
                x - b,
                y - b
        );
    }

    public NTxVector2D mul(double b) {
        return new NTxVector2D(
                x * b,
                y * b
        );
    }

    public double dot(NTxVector2D b) {
        return (
                x * b.x +
                        y * b.y
        );
    }
}
