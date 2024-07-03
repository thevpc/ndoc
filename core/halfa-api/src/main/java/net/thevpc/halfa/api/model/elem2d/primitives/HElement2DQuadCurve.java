package net.thevpc.halfa.api.model.elem2d.primitives;

import net.thevpc.halfa.api.model.HArrow;
import net.thevpc.halfa.api.model.elem2d.Element2DPrimitiveType;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;

public class HElement2DQuadCurve extends AbstractElement2DPrimitive {
    private HPoint2D from;
    private HPoint2D to;
    private HPoint2D ctrl;
    private HArrow startArrow;
    private HArrow endArrow;

    public HElement2DQuadCurve(HPoint2D from, HPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Element2DPrimitiveType type() {
        return Element2DPrimitiveType.QUAD_CURVE;
    }


    public HPoint2D getCtrl() {
        return ctrl;
    }

    public HElement2DQuadCurve setCtrl(HPoint2D ctrl) {
        this.ctrl = ctrl;
        return this;
    }

    public HPoint2D getFrom() {
        return from;
    }

    public HElement2DQuadCurve setFrom(HPoint2D from) {
        this.from = from;
        return this;
    }

    public HPoint2D getTo() {
        return to;
    }

    public HElement2DQuadCurve setTo(HPoint2D to) {
        this.to = to;
        return this;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public HElement2DQuadCurve setStartArrow(HArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public HElement2DQuadCurve setEndArrow(HArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
