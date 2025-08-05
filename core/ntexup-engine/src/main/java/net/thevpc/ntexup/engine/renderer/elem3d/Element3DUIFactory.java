package net.thevpc.ntexup.engine.renderer.elem3d;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxRenderState3D;
import net.thevpc.ntexup.api.renderer.NTxElement3DRenderer;

import java.util.*;

public class Element3DUIFactory {
    private Map<Class, NTxElement3DRenderer> map = new HashMap<>();

    public Element3DUIFactory() {
        ServiceLoader<NTxElement3DRenderer> serviceLoader = ServiceLoader.load(NTxElement3DRenderer.class);
        for (NTxElement3DRenderer element3DPrimitiveBuilder : serviceLoader) {
            register(element3DPrimitiveBuilder.forType(), element3DPrimitiveBuilder);
        }
    }

    void register(Class c, NTxElement3DRenderer f) {
        map.put(c, f);
    }

    public NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, NTxRenderState3D renderState) {
        if (e instanceof NtxElement3DPrimitive) {
            return new NtxElement3DPrimitive[]{(NtxElement3DPrimitive) e};
        }
        NTxElement3DRenderer i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e, renderState);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
