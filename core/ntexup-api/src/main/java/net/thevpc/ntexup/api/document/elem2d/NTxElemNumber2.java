package net.thevpc.ntexup.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NNumberElement;

public class NTxElemNumber2 {
    private NNumberElement x;
    private NNumberElement y;

    public NTxElemNumber2(NElement x, NElement y) {
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
