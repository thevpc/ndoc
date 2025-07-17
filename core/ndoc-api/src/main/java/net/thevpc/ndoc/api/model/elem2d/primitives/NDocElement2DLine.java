package net.thevpc.ndoc.api.model.elem2d.primitives;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.model.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;

public class NDocElement2DLine extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D from;
    private NDocPoint2D to;
    private HArrow startArrow;
    private HArrow endArrow;

    public NDocElement2DLine(NDocPoint2D from, NDocPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public NDocElement2DPrimitiveType type() {
        return NDocElement2DPrimitiveType.LINE;
    }

    public NDocPoint2D getFrom() {
        return from;
    }

    public NDocElement2DLine setFrom(NDocPoint2D from) {
        this.from = from;
        return this;
    }

    public NDocPoint2D getTo() {
        return to;
    }

    public NDocElement2DLine setTo(NDocPoint2D to) {
        this.to = to;
        return this;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public NDocElement2DLine setStartArrow(HArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public NDocElement2DLine setEndArrow(HArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
