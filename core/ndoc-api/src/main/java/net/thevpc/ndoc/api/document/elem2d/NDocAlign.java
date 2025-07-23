package net.thevpc.ndoc.api.document.elem2d;

import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

public enum NDocAlign implements NToElement {
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

    public NOptional<NDocDouble2> toPosition() {
        switch (this) {
            case TOP:
                return NOptional.of(new NDocDouble2(50, 0));
            case BOTTOM:
                return NOptional.of(new NDocDouble2(50, 100));
            case LEFT:
                return NOptional.of(new NDocDouble2(0, 50));
            case RIGHT:
                return NOptional.of(new NDocDouble2(100, 50));
            case TOP_LEFT:
                return NOptional.of(new NDocDouble2(0, 0));
            case CENTER:
                return NOptional.of(new NDocDouble2(50, 50));
            case TOP_RIGHT:
                return NOptional.of(new NDocDouble2(100, 0));
            case BOTTOM_RIGHT:
                return NOptional.of(new NDocDouble2(100, 100));
            case BOTTOM_LEFT:
                return NOptional.of(new NDocDouble2(0, 100));
            case NONE: {
                break;
            }
        }
        return NOptional.ofEmpty(NMsg.ofC("invalid Double2 from %s", this));
    }

    public static NOptional<NDocAlign> parse(String e) {
        try {
            String u = NNameFormat.CONST_NAME.format(NStringUtils.trim(e));
            NDocAlign p = valueOf(u);
            return NOptional.of(p);
        } catch (Exception ex) {
            //
        }
        e = NNameFormat.LOWER_KEBAB_CASE.format(NStringUtils.trim(e));
        return NOptional.ofNamedEmpty("HAlign " + e);
    }

    @Override
    public NElement toElement() {
        return NElement.ofName(NDocUtils.uid(name()));
    }
}
