package net.thevpc.ndoc.api.model.elem2d;

import net.thevpc.tson.TsonNumber;

public class TsonNumber2 {
    private TsonNumber x;
    private TsonNumber y;

    public TsonNumber2(TsonNumber x, TsonNumber y) {
        this.x = x;
        this.y = y;
    }

    public TsonNumber getX() {
        return x;
    }

    public TsonNumber getY() {
        return y;
    }
}
