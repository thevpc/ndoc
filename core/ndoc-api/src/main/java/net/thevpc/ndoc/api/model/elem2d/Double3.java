package net.thevpc.ndoc.api.model.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

public class Double3 implements NToElement {
    private Double x;
    private Double y;
    private Double z;

    public Double3(Number x, Number y, Number z) {
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
        return "(" + x + ", " + y + ", " + z + ')';
    }

    public Double3 mul(double wx, double wy, double wz) {
        return new Double3(
                x * wx,
                y * wy,
                z * wz
        );
    }

    @Override
    public NElement toElement() {
        return NElement.ofUplet(
                NElement.ofDouble(getX()),
                NElement.ofDouble(getY()),
                NElement.ofDouble(getZ())
        );
    }
}
