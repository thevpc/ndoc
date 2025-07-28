package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.ndoc.api.util.NDocUtils;

public class NDocBounds2 {
    private Double x;
    private Double y;
    private Double w;
    private Double h;

    public NDocBounds2(Number x, Number y, Number w, Number h) {
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

    public NDocBounds2 expand(NDocBounds2 s) {
        if (s == null) {
            return new NDocBounds2(x, y, w, h);
        }

        Double xx1 = NDocUtils.min(getMinX(), s.getMinX());
        Double yy1 = NDocUtils.min(getMinY(), s.getMinY());
        Double xx2 = NDocUtils.max(getMaxX(), s.getMaxX());
        Double yy2 = NDocUtils.max(getMaxY(), s.getMaxY());
        return new NDocBounds2(
                xx1,
                yy1,
                NDocUtils.doubleOf(xx2) - NDocUtils.doubleOf(xx1),
                NDocUtils.doubleOf(yy2) - NDocUtils.doubleOf(yy1)
        );
    }

}
