package net.thevpc.halfa.api.model;

public class Bounds2 {
    private Double x;
    private Double y;
    private Double w;
    private Double h;

    public Bounds2(Number x, Number y, Double w, Double h) {
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

}
