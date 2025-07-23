package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NNumberElement;

public class NDocElemNumber2 {
    private NNumberElement x;
    private NNumberElement y;

    public NDocElemNumber2(NElement x, NElement y) {
        this.x = (NNumberElement) x;
        this.y = (NNumberElement)y;
    }

    public NNumberElement getX() {
        return x;
    }

    public NNumberElement getY() {
        return y;
    }
}
