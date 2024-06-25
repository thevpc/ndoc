package net.thevpc.halfa.api.model.elem3d.primitives;

import net.thevpc.halfa.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.Element3DPrimitiveType;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;

public class Element3DPolygon extends AbstractElement3DPrimitive {
    private HPoint3D[] nodes;
    private boolean fill;
    private boolean contour;

    public Element3DPolygon(HPoint3D[] nodes, boolean fill, boolean contour) {
        this.nodes = nodes;
        this.fill = fill;
        this.contour = contour;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isContour() {
        return contour;
    }

    public HPoint3D[] getNodes() {
        return nodes;
    }

    @Override
    public Element3DPrimitiveType type() {
        return Element3DPrimitiveType.POLYGON;
    }
}
