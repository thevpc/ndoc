package net.thevpc.ntexup.engine.renderer.elem2d;

import net.thevpc.ntexup.api.document.elem2d.NtxElement2DPrimitive;
import net.thevpc.ntexup.api.document.elem2d.NtxElement2D;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class Element2DUIFactory {
    private Map<Class, NTxElement2DPrimitiveBuilder> map = new HashMap<>();

    public Element2DUIFactory() {
    }

    void register(Class c, NTxElement2DPrimitiveBuilder f) {
        map.put(c, f);
    }

    public NtxElement2DPrimitive[] toPrimitives(NtxElement2D e) {
        if (e instanceof NtxElement2DPrimitive) {
            return new NtxElement2DPrimitive[]{(NtxElement2DPrimitive) e};
        }
        NTxElement2DPrimitiveBuilder i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
