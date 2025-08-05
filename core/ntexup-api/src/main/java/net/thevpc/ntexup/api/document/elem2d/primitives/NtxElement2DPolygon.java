package net.thevpc.ntexup.api.document.elem2d.primitives;


import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveType;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;

public class NtxElement2DPolygon extends AbstractNtxElement2DPrimitive {
    private NTxPoint2D[] nodes;
    private boolean fill;
    private boolean contour;

    public NtxElement2DPolygon(NTxPoint2D[] nodes, boolean fill, boolean contour) {
        this.nodes = nodes;
        this.fill = fill;
        this.contour = contour;
    }

    public NtxElement2DPolygon setFill(boolean fill) {
        this.fill = fill;
        return this;
    }

    public NtxElement2DPolygon setContour(boolean contour) {
        this.contour = contour;
        return this;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isContour() {
        return contour;
    }

    public NTxPoint2D[] getNodes() {
        return nodes;
    }

    @Override
    public NTxElement2DPrimitiveType type() {
        return NTxElement2DPrimitiveType.POLYGON;
    }
}
