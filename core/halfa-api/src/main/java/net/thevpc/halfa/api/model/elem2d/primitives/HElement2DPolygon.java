package net.thevpc.halfa.api.model.elem2d.primitives;

import net.thevpc.halfa.api.model.elem2d.Element2DPrimitiveType;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;

public class HElement2DPolygon extends AbstractElement2DPrimitive {
    private HPoint2D[] nodes;
    private boolean fill;
    private boolean contour;

    public HElement2DPolygon(HPoint2D[] nodes, boolean fill, boolean contour) {
        this.nodes = nodes;
        this.fill=fill;
        this.contour=contour;
    }

    public HElement2DPolygon setFill(boolean fill) {
        this.fill = fill;
        return this;
    }

    public HElement2DPolygon setContour(boolean contour) {
        this.contour = contour;
        return this;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isContour() {
        return contour;
    }

    public HPoint2D[] getNodes() {
        return nodes;
    }

    @Override
    public Element2DPrimitiveType type() {
        return Element2DPrimitiveType.POLYGON;
    }
}
