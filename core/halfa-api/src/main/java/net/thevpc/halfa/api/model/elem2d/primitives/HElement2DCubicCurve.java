package net.thevpc.halfa.api.model.elem2d.primitives;

import net.thevpc.halfa.api.model.HArrow;
import net.thevpc.halfa.api.model.elem2d.Element2DPrimitiveType;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;

public class HElement2DCubicCurve extends AbstractElement2DPrimitive {
    private HPoint2D from;
    private HPoint2D to;
    private HPoint2D ctrl1;
    private HPoint2D ctrl2;
    private HArrow startArrow;
    private HArrow endArrow;

    public HElement2DCubicCurve(HPoint2D from, HPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Element2DPrimitiveType type() {
        return Element2DPrimitiveType.CUBIC_CURVE;
    }

    public HPoint2D getCtrl2() {
        return ctrl2;
    }

    public HElement2DCubicCurve setCtrl2(HPoint2D ctrl2) {
        this.ctrl2 = ctrl2;
        return this;
    }

    public HPoint2D getCtrl1() {
        return ctrl1;
    }

    public HElement2DCubicCurve setCtrl1(HPoint2D ctrl1) {
        this.ctrl1 = ctrl1;
        return this;
    }

    public HPoint2D getFrom() {
        return from;
    }

    public HElement2DCubicCurve setFrom(HPoint2D from) {
        this.from = from;
        return this;
    }

    public HPoint2D getTo() {
        return to;
    }

    public HElement2DCubicCurve setTo(HPoint2D to) {
        this.to = to;
        return this;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public HElement2DCubicCurve setStartArrow(HArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public HElement2DCubicCurve setEndArrow(HArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
