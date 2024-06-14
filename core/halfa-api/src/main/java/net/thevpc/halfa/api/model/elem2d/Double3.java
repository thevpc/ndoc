package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class Double3 implements ToTson {
    private Double x;
    private Double y;
    private Double z;

    public Double3(Number x, Number y,Number z) {
        this.x = x == null ? null : x.doubleValue();
        this.y = y == null ? null : y.doubleValue();
        this.z = z == null ? null : z.doubleValue();
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y+ ", " + z + ')';
    }

    public Double3 mul(double wx, double wy, double wz) {
        return new Double3(
                x * wx,
                y * wy,
                z * wz
        );
    }

    @Override
    public TsonElement toTson() {
        return Tson.ofUplet(
                Tson.ofDouble(getX()),
                Tson.ofDouble(getY()),
                Tson.ofDouble(getZ())
        ).build();
    }
}
