package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class Padding implements ToTson {
    private Double top;
    private Double right;
    private Double bottom;
    private Double left;

    public Padding(Double top, Double right, Double bottom, Double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public static Padding of(double v) {
        return new Padding(v, v, v, v);
    }

    public static Padding of(double t, double r) {
        return new Padding(t, r, t, r);
    }

    public static Padding of(double t, double rl, double b) {
        return new Padding(t, rl, b, rl);
    }

    public static Padding of(double t, double r, double b, double l) {
        return new Padding(t, r, b, l);
    }

    public Double getTop() {
        return top;
    }

    public Double getRight() {
        return right;
    }

    public Double getBottom() {
        return bottom;
    }

    public Double getLeft() {
        return left;
    }

    @Override
    public String toString() {
        return "(" + top
                + ", " + right
                + ", " + bottom
                + ", " + left
                + ')';
    }

    @Override
    public TsonElement toTson() {
        return Tson.ofUplet(
                Tson.ofDouble(getTop()),
                Tson.ofDouble(getRight()),
                Tson.ofDouble(getBottom()),
                Tson.ofDouble(getLeft())
        ).build();
    }

    public Padding mul(double w, double h) {
        return new Padding(
                top * h,
                top * w,
                bottom * h,
                left * w
        );
    }
}
