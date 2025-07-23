package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

import java.util.Objects;

public class NDocPoint2D implements NToElement {
    public double x, y;

    public NDocPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D asVector() {
        return new Vector2D(x, y);
    }

    public NDocPoint2D minus(NDocPoint2D b) {
        return new NDocPoint2D(
                x - b.x,
                y - b.y
        );
    }


    public double length() {
        return Math.sqrt(dot(this));
    }

    public NDocPoint2D plus(NDocPoint2D b) {
        return new NDocPoint2D(
                x + b.x,
                y + b.y
        );
    }

    public NDocPoint2D plus(double b) {
        return new NDocPoint2D(
                x + b,
                y + b
        );
    }

    public NDocPoint2D minus(double b) {
        return new NDocPoint2D(
                x - b,
                y - b
        );
    }

    public NDocPoint2D mul(double b) {
        return new NDocPoint2D(
                x * b,
                y * b
        );
    }

    public double dot(NDocPoint2D b) {
        return (
                x * b.x +
                        y * b.y
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NDocPoint2D point2D = (NDocPoint2D) o;
        return Double.compare(x, point2D.x) == 0 && Double.compare(y, point2D.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public NElement toElement() {
        return NElement.ofUplet(
                NElement.ofDouble(getX()),
                NElement.ofDouble(getY())
        );
    }

    public NDocPoint2D add(double x, double y) {
        return new NDocPoint2D(x+x, y+y);
    }
}
