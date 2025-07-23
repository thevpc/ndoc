package net.thevpc.ndoc.api.document.elem2d.primitives;


import net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;

public class NDocElement2DPolygon extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D[] nodes;
    private boolean fill;
    private boolean contour;

    public NDocElement2DPolygon(NDocPoint2D[] nodes, boolean fill, boolean contour) {
        this.nodes = nodes;
        this.fill = fill;
        this.contour = contour;
    }

    public NDocElement2DPolygon setFill(boolean fill) {
        this.fill = fill;
        return this;
    }

    public NDocElement2DPolygon setContour(boolean contour) {
        this.contour = contour;
        return this;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isContour() {
        return contour;
    }

    public NDocPoint2D[] getNodes() {
        return nodes;
    }

    @Override
    public NDocElement2DPrimitiveType type() {
        return NDocElement2DPrimitiveType.POLYGON;
    }
}
