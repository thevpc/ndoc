package net.thevpc.halfa.api.model;

public class Double2 {
    private Double x;
    private Double y;

    public Double2(Number x, Number y) {
        this.x = x == null ? null : x.doubleValue();
        this.y = y == null ? null : y.doubleValue();
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }

}
