package net.thevpc.halfa.engine.renderer.elem3d;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;
import net.thevpc.halfa.spi.HNode3DSimplifier;

import java.util.*;

public class Element3DUIFactory {
    private Map<Class, HNode3DSimplifier> map = new HashMap<>();

    public Element3DUIFactory() {
        ServiceLoader<HNode3DSimplifier> serviceLoader = ServiceLoader.load(HNode3DSimplifier.class);
        for (HNode3DSimplifier element3DPrimitiveBuilder : serviceLoader) {
            register(element3DPrimitiveBuilder.forType(), element3DPrimitiveBuilder);
        }
    }

    void register(Class c, HNode3DSimplifier f) {
        map.put(c, f);
    }

    public HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState) {
        if (e instanceof HElement3DPrimitive) {
            return new HElement3DPrimitive[]{(HElement3DPrimitive) e};
        }
        HNode3DSimplifier i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e, renderState);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
