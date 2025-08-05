package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.NTxArrow;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveType;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;

public class NtxElement2DCubicCurve extends AbstractNtxElement2DPrimitive {
    private NTxPoint2D from;
    private NTxPoint2D to;
    private NTxPoint2D ctrl1;
    private NTxPoint2D ctrl2;
    private NTxArrow startArrow;
    private NTxArrow endArrow;

    public NtxElement2DCubicCurve(NTxPoint2D from, NTxPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public NTxElement2DPrimitiveType type() {
        return NTxElement2DPrimitiveType.CUBIC_CURVE;
    }

    public NTxPoint2D getCtrl2() {
        return ctrl2;
    }

    public NtxElement2DCubicCurve setCtrl2(NTxPoint2D ctrl2) {
        this.ctrl2 = ctrl2;
        return this;
    }

    public NTxPoint2D getCtrl1() {
        return ctrl1;
    }

    public NtxElement2DCubicCurve setCtrl1(NTxPoint2D ctrl1) {
        this.ctrl1 = ctrl1;
        return this;
    }

    public NTxPoint2D getFrom() {
        return from;
    }

    public NtxElement2DCubicCurve setFrom(NTxPoint2D from) {
        this.from = from;
        return this;
    }

    public NTxPoint2D getTo() {
        return to;
    }

    public NtxElement2DCubicCurve setTo(NTxPoint2D to) {
        this.to = to;
        return this;
    }

    public NTxArrow getStartArrow() {
        return startArrow;
    }

    public NtxElement2DCubicCurve setStartArrow(NTxArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public NTxArrow getEndArrow() {
        return endArrow;
    }

    public NtxElement2DCubicCurve setEndArrow(NTxArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
