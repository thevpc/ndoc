package net.thevpc.ndoc.api.document.elem2d.primitives;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;

public class NDocElement2DCubicCurve extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D from;
    private NDocPoint2D to;
    private NDocPoint2D ctrl1;
    private NDocPoint2D ctrl2;
    private HArrow startArrow;
    private HArrow endArrow;

    public NDocElement2DCubicCurve(NDocPoint2D from, NDocPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public NDocElement2DPrimitiveType type() {
        return NDocElement2DPrimitiveType.CUBIC_CURVE;
    }

    public NDocPoint2D getCtrl2() {
        return ctrl2;
    }

    public NDocElement2DCubicCurve setCtrl2(NDocPoint2D ctrl2) {
        this.ctrl2 = ctrl2;
        return this;
    }

    public NDocPoint2D getCtrl1() {
        return ctrl1;
    }

    public NDocElement2DCubicCurve setCtrl1(NDocPoint2D ctrl1) {
        this.ctrl1 = ctrl1;
        return this;
    }

    public NDocPoint2D getFrom() {
        return from;
    }

    public NDocElement2DCubicCurve setFrom(NDocPoint2D from) {
        this.from = from;
        return this;
    }

    public NDocPoint2D getTo() {
        return to;
    }

    public NDocElement2DCubicCurve setTo(NDocPoint2D to) {
        this.to = to;
        return this;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public NDocElement2DCubicCurve setStartArrow(HArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public NDocElement2DCubicCurve setEndArrow(HArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
