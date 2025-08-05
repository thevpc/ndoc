package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.NTxArrow;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveType;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;

public class NtxElement2DQuadCurve extends AbstractNtxElement2DPrimitive {
    private NTxPoint2D from;
    private NTxPoint2D to;
    private NTxPoint2D ctrl;
    private NTxArrow startArrow;
    private NTxArrow endArrow;

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

    public NTxArrow getStartArrow() {
        return startArrow;
    }

    public NtxElement2DQuadCurve setStartArrow(NTxArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public NTxArrow getEndArrow() {
        return endArrow;
    }

    public NtxElement2DQuadCurve setEndArrow(NTxArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
