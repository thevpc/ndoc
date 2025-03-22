package net.thevpc.ndoc.api.model.elem2d.primitives;

import net.thevpc.ndoc.api.model.elem2d.Element2DPrimitiveType;
import net.thevpc.ndoc.api.model.elem2d.HPoint2D;

public class HElement2DPolyline extends AbstractElement2DPrimitive {
    private HPoint2D[] nodes;

    public HElement2DPolyline(HPoint2D[] nodes) {
        this.nodes = nodes;
    }

    public HPoint2D[] getNodes() {
        return nodes;
    }

    @Override
    public Element2DPrimitiveType type() {
        return Element2DPrimitiveType.POLYLINE;
    }
}
