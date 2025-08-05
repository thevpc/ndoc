package net.thevpc.ntexup.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

public class NTxPadding implements NToElement {
    private Double top;
    private Double right;
    private Double bottom;
    private Double left;

    public NTxPadding(Double left, Double top, Double right, Double bottom) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public static NTxPadding of(double v) {
        return new NTxPadding(v, v, v, v);
    }

    public static NTxPadding of(double l, double t) {
        return new NTxPadding(l, t, l, t);
    }

    public static NTxPadding of(double rl, double t, double b) {
        return new NTxPadding(rl, t, b, t);
    }

    public static NTxPadding of(double l, double t, double b, double r) {
        return new NTxPadding(l, t, b, r);
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

    public NTxPadding mul(double w, double h) {
        return new NTxPadding(
                left * w,
                top * h,
                right * w,
                bottom * h
        );
    }
}
