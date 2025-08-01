package net.thevpc.ndoc.api.document.elem2d.primitives;

import net.thevpc.ndoc.api.document.NDocArrow;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;

public class NDocElement2DQuadCurve extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D from;
    private NDocPoint2D to;
    private NDocPoint2D ctrl;
    private NDocArrow startArrow;
    private NDocArrow endArrow;

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

    public NDocArrow getStartArrow() {
        return startArrow;
    }

    public NDocElement2DQuadCurve setStartArrow(NDocArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public NDocArrow getEndArrow() {
        return endArrow;
    }

    public NDocElement2DQuadCurve setEndArrow(NDocArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
