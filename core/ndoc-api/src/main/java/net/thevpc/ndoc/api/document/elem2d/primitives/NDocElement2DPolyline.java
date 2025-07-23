package net.thevpc.ndoc.api.document.elem2d.primitives;

import net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;

public class NDocElement2DPolyline extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D[] nodes;

    public NDocElement2DPolyline(NDocPoint2D[] nodes) {
        this.nodes = nodes;
    }

    public NDocPoint2D[] getNodes() {
        return nodes;
    }

    @Override
    public NDocElement2DPrimitiveType type() {
        return NDocElement2DPrimitiveType.POLYLINE;
    }
}
