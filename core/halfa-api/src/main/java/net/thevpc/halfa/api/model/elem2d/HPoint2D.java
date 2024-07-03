package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.Objects;

public class HPoint2D implements ToTson {
    public double x, y;

    public HPoint2D(double x, double y) {
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

    public HPoint2D minus(HPoint2D b) {
        return new HPoint2D(
                x - b.x,
                y - b.y
        );
    }


    public double length() {
        return Math.sqrt(dot(this));
    }

    public HPoint2D plus(HPoint2D b) {
        return new HPoint2D(
                x + b.x,
                y + b.y
        );
    }

    public HPoint2D plus(double b) {
        return new HPoint2D(
                x + b,
                y + b
        );
    }

    public HPoint2D minus(double b) {
        return new HPoint2D(
                x - b,
                y - b
        );
    }

    public HPoint2D mul(double b) {
        return new HPoint2D(
                x * b,
                y * b
        );
    }

    public double dot(HPoint2D b) {
        return (
                x * b.x +
                        y * b.y
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HPoint2D point2D = (HPoint2D) o;
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
    public TsonElement toTson() {
        return Tson.ofUplet(
                Tson.ofDouble(getX()),
                Tson.ofDouble(getY())
        ).build();
    }

    public HPoint2D add(double x, double y) {
        return new HPoint2D(x+x, y+y);
    }
}
