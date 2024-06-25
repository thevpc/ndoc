package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class HStyleValue {
    public static NOptional<Double2> toDouble2(Object svv) {
        if (svv == null) {
            return NOptional.ofNamedEmpty("Double2");
        }
        if (svv instanceof Double2) {
            return NOptional.of((Double2) svv);
        }
        if (svv instanceof HAlign) {
            return ((HAlign) svv).toPosition();
        }
        return NOptional.ofEmpty(NMsg.ofC("invalid Double2 from %s", svv));
    }
}
