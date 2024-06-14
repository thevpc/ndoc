package net.thevpc.halfa.engine.renderer.screen.renderers.elem3d;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DBox;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DGroup;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DSurface;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DUVSphere;

import java.util.*;

public class Element3DUIFactory {
    private Map<Class, Element3DPrimitiveBuilder> map = new HashMap<>();

    public Element3DUIFactory() {
        register(Element3DBox.class, new Element3DBoxPrimitiveBuilder());
        register(Element3DGroup.class, new Element3DGroupPrimitiveBuilder());
        register(Element3DSurface.class, new Element3DSurfacePrimitiveBuilder());
        register(Element3DUVSphere.class, new Element3DUVSpherePrimitiveBuilder());
    }

    void register(Class c, Element3DPrimitiveBuilder f) {
        map.put(c, f);
    }

    public HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState) {
        if (e instanceof HElement3DPrimitive) {
            return new HElement3DPrimitive[]{(HElement3DPrimitive) e};
        }
        Element3DPrimitiveBuilder i = map.get(e.getClass());
        if (i != null) {
            return i.toPrimitives(e, renderState);
        }
        throw new IllegalArgumentException("Not Found Primitive Builder for " + e.getClass());
    }

}
