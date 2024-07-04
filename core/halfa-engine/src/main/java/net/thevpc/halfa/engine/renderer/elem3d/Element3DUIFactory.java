package net.thevpc.halfa.engine.renderer.elem3d;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;
import net.thevpc.halfa.spi.HElement3DRenderer;

import java.util.*;

public class Element3DUIFactory {
    private Map<Class, HElement3DRenderer> map = new HashMap<>();

    public Element3DUIFactory() {
        ServiceLoader<HElement3DRenderer> serviceLoader = ServiceLoader.load(HElement3DRenderer.class);
        for (HElement3DRenderer element3DPrimitiveBuilder : serviceLoader) {
            register(element3DPrimitiveBuilder.forType(), element3DPrimitiveBuilder);
        }
    }

    void register(Class c, HElement3DRenderer f) {
        map.put(c, f);
    }

    public HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState) {
        if (e instanceof HElement3DPrimitive) {
            return new HElement3DPrimitive[]{(HElement3DPrimitive) e};
        }
        HElement3DRenderer i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e, renderState);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
