package net.thevpc.ntexup.engine.renderer.elem3d;

import net.thevpc.ntexup.api.document.elem3d.NtxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxRenderState3D;
import net.thevpc.ntexup.api.document.elem3d.composite.NtxElement3DGroup;
import net.thevpc.ntexup.api.renderer.NTxElement3DRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Element3DGroupPrimitiveBuilder implements NTxElement3DRenderer {
    @Override
    public Class<? extends NtxElement3D> forType() {
        return NtxElement3DGroup.class;
    }

    @Override
    public NtxElement3DPrimitive[] toPrimitives(NtxElement3D e, NTxRenderState3D renderState) {
        NtxElement3DGroup ee = (NtxElement3DGroup) e;
        List<NtxElement3D> elements = ee.getElements();
        List<NtxElement3DPrimitive> all = new ArrayList<>();
        for (NtxElement3D element : elements) {
            all.addAll(Arrays.asList(renderState.toPrimitives(element)));
        }
        return all.toArray(new NtxElement3DPrimitive[0]);
    }
}
