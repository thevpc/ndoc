package net.thevpc.ndoc.api.model.elem2d.primitives;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.model.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;

public class NDocElement2DQuadCurve extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D from;
    private NDocPoint2D to;
    private NDocPoint2D ctrl;
    private HArrow startArrow;
    private HArrow endArrow;

    public NDocElement2DQuadCurve(NDocPoint2D from, NDocPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public NDocElement2DPrimitiveType type() {
        return NDocElement2DPrimitiveType.QUAD_CURVE;
    }


    public NDocPoint2D getCtrl() {
        return ctrl;
    }

    public NDocElement2DQuadCurve setCtrl(NDocPoint2D ctrl) {
        this.ctrl = ctrl;
        return this;
    }

    public NDocPoint2D getFrom() {
        return from;
    }

    public NDocElement2DQuadCurve setFrom(NDocPoint2D from) {
        this.from = from;
        return this;
    }

    public NDocPoint2D getTo() {
        return to;
    }

    public NDocElement2DQuadCurve setTo(NDocPoint2D to) {
        this.to = to;
        return this;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public NDocElement2DQuadCurve setStartArrow(HArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public NDocElement2DQuadCurve setEndArrow(HArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
