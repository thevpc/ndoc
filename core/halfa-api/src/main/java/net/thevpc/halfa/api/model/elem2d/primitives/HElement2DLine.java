package net.thevpc.halfa.api.model.elem2d.primitives;

import net.thevpc.halfa.api.model.HArrow;
import net.thevpc.halfa.api.model.elem2d.Element2DPrimitiveType;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;

public class HElement2DLine extends AbstractElement2DPrimitive {
    private HPoint2D from;
    private HPoint2D to;
    private HArrow startArrow;
    private HArrow endArrow;

    public HElement2DLine(HPoint2D from, HPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Element2DPrimitiveType type() {
        return Element2DPrimitiveType.LINE;
    }

    public HPoint2D getFrom() {
        return from;
    }

    public HElement2DLine setFrom(HPoint2D from) {
        this.from = from;
        return this;
    }

    public HPoint2D getTo() {
        return to;
    }

    public HElement2DLine setTo(HPoint2D to) {
        this.to = to;
        return this;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public HElement2DLine setStartArrow(HArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public HElement2DLine setEndArrow(HArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
