package net.thevpc.ndoc.api.model.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NToElement;

public class Int2 implements NToElement {
    private Integer x;
    private Integer y;

    public Int2(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }

    @Override
    public NElement toElement() {
        NElements elem = NElements.of();
        return elem.ofUplet(
                elem.ofInt(getX()),
                elem.ofInt(getY())
        );
    }

}
