package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;

public class NTxEditableBounds2 {
    public Double x;
    public Double y;
    public Double width;
    public Double height;

    public NTxEditableBounds2() {
    }

    public NTxEditableBounds2(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + width + ", " + height + ')';
    }
    public NTxBounds2 toBounds2() {
        return new NTxBounds2(x, y, width, height);
    }
}
