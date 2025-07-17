package net.thevpc.ndoc.api.model.elem3d.primitives;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitiveType;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;

public class NDocElement3DPolyline extends AbstractElement3DPrimitive {
    private NDocPoint3D[] nodes;

    public NDocElement3DPolyline(NDocPoint3D[] nodes) {
        this.nodes = nodes;
    }

    public NDocPoint3D[] getNodes() {
        return nodes;
    }

    @Override
    public NDocElement3DPrimitiveType type() {
        return NDocElement3DPrimitiveType.POLYLINE;
    }
}
