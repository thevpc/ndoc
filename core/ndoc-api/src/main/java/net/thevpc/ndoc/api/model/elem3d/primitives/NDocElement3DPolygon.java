package net.thevpc.ndoc.api.model.elem3d.primitives;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitiveType;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;

public class NDocElement3DPolygon extends AbstractElement3DPrimitive {
    private NDocPoint3D[] nodes;
    private boolean fill;
    private boolean contour;

    public NDocElement3DPolygon(NDocPoint3D[] nodes, boolean fill, boolean contour) {
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

    public NDocPoint3D[] getNodes() {
        return nodes;
    }

    @Override
    public NDocElement3DPrimitiveType type() {
        return NDocElement3DPrimitiveType.POLYGON;
    }
}
