package net.thevpc.halfa.api.model.elem2d;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D minus(Vector2D b) {
        return new Vector2D(
                x - b.x,
                y - b.y
        );
    }


    public double norm() {
        return Math.sqrt(dot(this));
    }

    public Vector2D normalize() {
        double lrcp = 1.0 / Math.sqrt(dot(this));
        return new Vector2D(this.x * lrcp, this.y * lrcp);
    }

    public Vector2D plus(Vector2D b) {
        return new Vector2D(
                x + b.x,
                y + b.y
        );
    }

    public Vector2D plus(double b) {
        return new Vector2D(
                x + b,
                y + b
        );
    }

    public Vector2D minus(double b) {
        return new Vector2D(
                x - b,
                y - b
        );
    }

    public Vector2D mul(double b) {
        return new Vector2D(
                x * b,
                y * b
        );
    }

    public double dot(Vector2D b) {
        return (
                x * b.x +
                        y * b.y
        );
    }
}
