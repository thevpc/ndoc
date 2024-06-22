package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonNumber;

public class Rotation implements ToTson {
    private TsonElement angle;
    private TsonElement x;
    private TsonElement y;

    public Rotation(TsonElement angle, TsonElement x, TsonElement y) {
        this.angle = angle;
        this.x = x;
        this.y = y;
    }

    public TsonElement getAngle() {
        return angle;
    }

    public TsonElement getX() {
        return x;
    }

    public TsonElement getY() {
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
                angle,
                x,
                y
        ).build();
    }

}
