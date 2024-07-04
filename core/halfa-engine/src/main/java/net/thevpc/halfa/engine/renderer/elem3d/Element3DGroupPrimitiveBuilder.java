package net.thevpc.halfa.engine.renderer.elem3d;

import net.thevpc.halfa.api.model.elem3d.HElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.HElement3D;
import net.thevpc.halfa.api.model.elem3d.RenderState3D;
import net.thevpc.halfa.api.model.elem3d.composite.Element3DGroup;
import net.thevpc.halfa.spi.HElement3DRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Element3DGroupPrimitiveBuilder implements HElement3DRenderer {
    @Override
    public Class<? extends HElement3D> forType() {
        return Element3DGroup.class;
    }

    @Override
    public HElement3DPrimitive[] toPrimitives(HElement3D e, RenderState3D renderState) {
        Element3DGroup ee = (Element3DGroup) e;
        List<HElement3D> elements = ee.getElements();
        List<HElement3DPrimitive> all = new ArrayList<>();
        for (HElement3D element : elements) {
            all.addAll(Arrays.asList(renderState.toPrimitives(element)));
        }
        return all.toArray(new HElement3DPrimitive[0]);
    }
}
