package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public enum HAlign implements ToTson {
    TOP_LEFT,
    TOP_RIGHT,
    TOP,
    LEFT,
    RIGHT,
    CENTER,
    BOTTOM,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    NONE;

    public NOptional<Double2> toPosition() {
        switch (this) {
            case TOP:
                return NOptional.of(new Double2(50, 0));
            case BOTTOM:
                return NOptional.of(new Double2(50, 100));
            case LEFT:
                return NOptional.of(new Double2(0, 50));
            case RIGHT:
                return NOptional.of(new Double2(100, 50));
            case TOP_LEFT:
                return NOptional.of(new Double2(0, 0));
            case CENTER:
                return NOptional.of(new Double2(50, 50));
            case TOP_RIGHT:
                return NOptional.of(new Double2(100, 0));
            case BOTTOM_RIGHT:
                return NOptional.of(new Double2(100, 100));
            case BOTTOM_LEFT:
                return NOptional.of(new Double2(0, 100));
            case NONE: {
                break;
            }
        }
        return NOptional.ofEmpty(NMsg.ofC("invalid Double2 from %s", this));
    }

    public static NOptional<HAlign> parse(String e) {
        try {
            String u = NNameFormat.CONST_NAME.format(NStringUtils.trim(e));
            HAlign p = valueOf(u);
            return NOptional.of(p);
        } catch (Exception ex) {
            //
        }
        e = NNameFormat.LOWER_KEBAB_CASE.format(NStringUtils.trim(e));
        return NOptional.ofNamedEmpty("HAlign " + e);
    }

    @Override
    public TsonElement toTson() {
        return Tson.of(HUtils.uid(name()));
    }
}
