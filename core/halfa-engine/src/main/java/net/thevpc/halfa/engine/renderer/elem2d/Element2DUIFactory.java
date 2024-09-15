package net.thevpc.halfa.engine.renderer.elem2d;

import net.thevpc.halfa.api.model.elem2d.Element2DPrimitive;
import net.thevpc.halfa.api.model.elem2d.HElement2D;
import net.thevpc.halfa.spi.nodes.Element2DPrimitiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class Element2DUIFactory {
    private Map<Class, Element2DPrimitiveBuilder> map = new HashMap<>();

    public Element2DUIFactory() {
    }

    void register(Class c, Element2DPrimitiveBuilder f) {
        map.put(c, f);
    }

    public Element2DPrimitive[] toPrimitives(HElement2D e) {
        if (e instanceof Element2DPrimitive) {
            return new Element2DPrimitive[]{(Element2DPrimitive) e};
        }
        Element2DPrimitiveBuilder i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
