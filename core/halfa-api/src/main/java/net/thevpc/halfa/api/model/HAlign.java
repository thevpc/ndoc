package net.thevpc.halfa.api.model;

import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringUtils;

public enum HAlign {
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
}
