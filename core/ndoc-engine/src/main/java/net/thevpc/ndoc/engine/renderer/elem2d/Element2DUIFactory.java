package net.thevpc.ndoc.engine.renderer.elem2d;

import net.thevpc.ndoc.api.model.elem2d.NDocElement2DPrimitive;
import net.thevpc.ndoc.api.model.elem2d.NDocElement2D;
import net.thevpc.ndoc.spi.nodes.NDocElement2DPrimitiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class Element2DUIFactory {
    private Map<Class, NDocElement2DPrimitiveBuilder> map = new HashMap<>();

    public Element2DUIFactory() {
    }

    void register(Class c, NDocElement2DPrimitiveBuilder f) {
        map.put(c, f);
    }

    public NDocElement2DPrimitive[] toPrimitives(NDocElement2D e) {
        if (e instanceof NDocElement2DPrimitive) {
            return new NDocElement2DPrimitive[]{(NDocElement2DPrimitive) e};
        }
        NDocElement2DPrimitiveBuilder i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
