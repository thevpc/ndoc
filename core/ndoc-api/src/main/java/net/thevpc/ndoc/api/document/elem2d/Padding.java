package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

public class Padding implements NToElement {
    private Double top;
    private Double right;
    private Double bottom;
    private Double left;

    public Padding(Double left,Double top, Double right, Double bottom) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public static Padding of(double v) {
        return new Padding(v, v, v, v);
    }

    public static Padding of(double l, double t) {
        return new Padding(l, t, l, t);
    }

    public static Padding of(double rl, double t, double b) {
        return new Padding(rl, t, b, t);
    }

    public static Padding of(double l, double t, double b, double r) {
        return new Padding(l, t, b, r);
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
        return "(" + left
                + ", " + top
                + ", " + right
                + ", " + bottom
                + ')';
    }

    @Override
    public NElement toElement() {
        return NElement.ofUplet(
                NElement.ofDouble(getLeft()),
                NElement.ofDouble(getTop()),
                NElement.ofDouble(getRight()),
                NElement.ofDouble(getBottom())
        );
    }

    public Padding mul(double w, double h) {
        return new Padding(
                left * w,
                top * h,
                right * w,
                bottom * h
        );
    }
}
