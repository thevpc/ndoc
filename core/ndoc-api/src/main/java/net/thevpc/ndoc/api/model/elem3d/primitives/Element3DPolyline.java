package net.thevpc.ndoc.api.model.elem3d.primitives;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.Element3DPrimitiveType;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;

public class Element3DPolyline extends AbstractElement3DPrimitive {
    private HPoint3D[] nodes;

    public Element3DPolyline(HPoint3D[] nodes) {
        this.nodes = nodes;
    }

    public HPoint3D[] getNodes() {
        return nodes;
    }

    @Override
    public Element3DPrimitiveType type() {
        return Element3DPrimitiveType.POLYLINE;
    }
}
