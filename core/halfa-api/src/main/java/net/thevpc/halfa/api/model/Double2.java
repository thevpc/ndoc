package net.thevpc.halfa.api.model;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class Double2 implements ToTson {
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

    public Double2 mul(double w, double h) {
        return new Double2(
                x * w,
                y * h
        );
    }

    @Override
    public TsonElement toTson() {
        return Tson.ofUplet(
                Tson.ofDouble(getX()),
                Tson.ofDouble(getY())
        ).build();
    }
}
