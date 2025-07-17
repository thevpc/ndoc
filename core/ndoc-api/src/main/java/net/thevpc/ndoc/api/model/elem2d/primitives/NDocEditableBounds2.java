package net.thevpc.ndoc.api.model.elem2d.primitives;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;

public class NDocEditableBounds2 {
    public Double x;
    public Double y;
    public Double width;
    public Double height;

    public NDocEditableBounds2() {
    }

    public NDocEditableBounds2(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + width + ", " + height + ')';
    }
    public NDocBounds2 toBounds2() {
        return new NDocBounds2(x, y, width, height);
    }
}
