package net.thevpc.ndoc.api.document.elem2d.primitives;

import net.thevpc.ndoc.api.document.NDocArrow;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DPrimitiveType;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;

public class NDocElement2DLine extends AbstractNDocElement2DPrimitive {
    private NDocPoint2D from;
    private NDocPoint2D to;
    private NDocArrow startArrow;
    private NDocArrow endArrow;

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

    public NDocArrow getStartArrow() {
        return startArrow;
    }

    public NDocElement2DLine setStartArrow(NDocArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public NDocArrow getEndArrow() {
        return endArrow;
    }

    public NDocElement2DLine setEndArrow(NDocArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
