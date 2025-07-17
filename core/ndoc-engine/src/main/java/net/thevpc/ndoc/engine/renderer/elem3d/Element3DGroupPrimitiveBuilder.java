package net.thevpc.ndoc.engine.renderer.elem3d;

import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3D;
import net.thevpc.ndoc.api.model.elem3d.RenderState3D;
import net.thevpc.ndoc.api.model.elem3d.composite.NDocElement3DGroup;
import net.thevpc.ndoc.spi.NDocElement3DRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Element3DGroupPrimitiveBuilder implements NDocElement3DRenderer {
    @Override
    public Class<? extends NDocElement3D> forType() {
        return NDocElement3DGroup.class;
    }

    @Override
    public NDocElement3DPrimitive[] toPrimitives(NDocElement3D e, RenderState3D renderState) {
        NDocElement3DGroup ee = (NDocElement3DGroup) e;
        List<NDocElement3D> elements = ee.getElements();
        List<NDocElement3DPrimitive> all = new ArrayList<>();
        for (NDocElement3D element : elements) {
            all.addAll(Arrays.asList(renderState.toPrimitives(element)));
        }
        return all.toArray(new NDocElement3DPrimitive[0]);
    }
}
