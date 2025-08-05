package net.thevpc.ntexup.engine.renderer.elem3d;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.RenderState3D;
import net.thevpc.ntexup.api.renderer.NDocElement3DRenderer;

import java.util.*;

public class Element3DUIFactory {
    private Map<Class, NDocElement3DRenderer> map = new HashMap<>();

    public Element3DUIFactory() {
        ServiceLoader<NDocElement3DRenderer> serviceLoader = ServiceLoader.load(NDocElement3DRenderer.class);
        for (NDocElement3DRenderer element3DPrimitiveBuilder : serviceLoader) {
            register(element3DPrimitiveBuilder.forType(), element3DPrimitiveBuilder);
        }
    }

    void register(Class c, NDocElement3DRenderer f) {
        map.put(c, f);
    }

    public NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, RenderState3D renderState) {
        if (e instanceof NtxElement3DPrimitive) {
            return new NtxElement3DPrimitive[]{(NtxElement3DPrimitive) e};
        }
        NDocElement3DRenderer i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e, renderState);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
