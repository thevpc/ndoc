package net.thevpc.ndoc.api.style;

import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

public class HStyleValue {
    public static NOptional<NDocDouble2> toDouble2(Object svv) {
        if (svv == null) {
            return NOptional.ofNamedEmpty("Double2");
        }
        if (svv instanceof NDocDouble2) {
            return NOptional.of((NDocDouble2) svv);
        }
        if (svv instanceof NDocAlign) {
            return ((NDocAlign) svv).toPosition();
        }
        return NOptional.ofEmpty(NMsg.ofC("invalid Double2 from %s", svv));
    }
}
