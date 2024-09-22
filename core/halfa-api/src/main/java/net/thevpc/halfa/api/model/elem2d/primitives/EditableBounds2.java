package net.thevpc.halfa.api.model.elem2d.primitives;

import net.thevpc.halfa.api.model.elem2d.Bounds2;

public class EditableBounds2 {
    public Double x;
    public Double y;
    public Double width;
    public Double height;

    public EditableBounds2() {
    }

    public EditableBounds2(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + width + ", " + height + ')';
    }
    public Bounds2 toBounds2() {
        return new Bounds2(x, y, width, height);
    }
}
