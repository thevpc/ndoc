package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.NDocArrow;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveType;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;

public class NtxElement2DQuadCurve extends AbstractNtxElement2DPrimitive {
    private NTxPoint2D from;
    private NTxPoint2D to;
    private NTxPoint2D ctrl;
    private NDocArrow startArrow;
    private NDocArrow endArrow;

    public NtxElement2DQuadCurve(NTxPoint2D from, NTxPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public NTxElement2DPrimitiveType type() {
        return NTxElement2DPrimitiveType.QUAD_CURVE;
    }


    public NTxPoint2D getCtrl() {
        return ctrl;
    }

    public NtxElement2DQuadCurve setCtrl(NTxPoint2D ctrl) {
        this.ctrl = ctrl;
        return this;
    }

    public NTxPoint2D getFrom() {
        return from;
    }

    public NtxElement2DQuadCurve setFrom(NTxPoint2D from) {
        this.from = from;
        return this;
    }

    public NTxPoint2D getTo() {
        return to;
    }

    public NtxElement2DQuadCurve setTo(NTxPoint2D to) {
        this.to = to;
        return this;
    }

    public NDocArrow getStartArrow() {
        return startArrow;
    }

    public NtxElement2DQuadCurve setStartArrow(NDocArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public NDocArrow getEndArrow() {
        return endArrow;
    }

    public NtxElement2DQuadCurve setEndArrow(NDocArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
