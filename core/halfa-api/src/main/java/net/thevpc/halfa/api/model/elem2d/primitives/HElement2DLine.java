package net.thevpc.halfa.api.model.elem2d.primitives;

import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem2d.Element2DPrimitiveType;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;

public class HElement2DLine extends AbstractElement2DPrimitive {
    private HPoint2D from;
    private HPoint2D to;
    private HArrayHead startType;
    private HArrayHead endType;

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

    public HArrayHead getStartType() {
        return startType;
    }

    public HElement2DLine setStartType(HArrayHead startType) {
        this.startType = startType;
        return this;
    }

    public HArrayHead getEndType() {
        return endType;
    }

    public HElement2DLine setEndType(HArrayHead toType) {
        this.endType = toType;
        return this;
    }
}
