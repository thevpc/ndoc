package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

public class NDocDouble3 implements NToElement {
    private Double x;
    private Double y;
    private Double z;

    public NDocDouble3(Number x, Number y, Number z) {
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

    public NDocDouble3 mul(double wx, double wy, double wz) {
        return new NDocDouble3(
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
