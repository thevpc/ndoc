package net.thevpc.halfa.api.model;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class Int2 implements ToTson {
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
    public TsonElement toTson() {
        return Tson.ofUplet(
                Tson.ofInt(getX()),
                Tson.ofInt(getY())
        ).build();
    }

}
