package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class Rotation implements ToTson {
    private Double angle;
    private Double x;
    private Double y;

    public Rotation(Number angle, Number x,Number y) {
        this.angle = HUtils.doubleOf(angle);
        this.x = HUtils.doubleOf(x);
        this.y = HUtils.doubleOf(y);
    }

    public Double getAngle() {
        return angle;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + angle
                + ", " + x
                + ", " + y
                + ')';
    }

    @Override
    public TsonElement toTson() {
        return Tson.ofUplet(
                Tson.ofDouble(angle),
                Tson.ofDouble(x),
                Tson.ofDouble(y)
        ).build();
    }

}
