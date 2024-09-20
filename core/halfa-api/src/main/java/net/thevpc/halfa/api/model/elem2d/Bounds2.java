package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.halfa.api.util.HUtils;

public class Bounds2 {
    private Double x;
    private Double y;
    private Double w;
    private Double h;

    public Bounds2(Number x, Number y, Number w, Number h) {
        this.x = x == null ? null : x.doubleValue();
        this.y = y == null ? null : y.doubleValue();
        this.w = w == null ? null : w.doubleValue();
        this.h = h == null ? null : h.doubleValue();
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getWidth() {
        return w;
    }

    public Double getHeight() {
        return h;
    }

    public Double getMinX() {
        return x;
    }

    public Double getCenterX() {
        if (x != null && w != null) {
            return x + w / 2;
        }
        return null;
    }

    public Double getCenterY() {
        if (y != null && h != null) {
            return y + h / 2;
        }
        return null;
    }

    public Double getMinY() {
        return y;
    }

    public Double getMaxX() {
        if (x == null || w == null) {
            return null;
        }
        return x + w;
    }

    public Double getMaxY() {
        if (y == null || h == null) {
            return null;
        }
        return y + h;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + w + ", " + h + ')';
    }

    public Bounds2 expand(Bounds2 s) {
        if (s == null) {
            return new Bounds2(x, y, w, h);
        }

        Double xx1 = net.thevpc.halfa.api.util.HUtils.min(getMinX(), s.getMinX());
        Double yy1 = net.thevpc.halfa.api.util.HUtils.min(getMinY(), s.getMinY());
        Double xx2 = net.thevpc.halfa.api.util.HUtils.max(getMaxX(), s.getMaxX());
        Double yy2 = net.thevpc.halfa.api.util.HUtils.max(getMaxY(), s.getMaxY());
        return new Bounds2(
                xx1,
                yy1,
                net.thevpc.halfa.api.util.HUtils.doubleOf(xx2) - net.thevpc.halfa.api.util.HUtils.doubleOf(xx1),
                net.thevpc.halfa.api.util.HUtils.doubleOf(yy2) - HUtils.doubleOf(yy1)
        );
    }

}
