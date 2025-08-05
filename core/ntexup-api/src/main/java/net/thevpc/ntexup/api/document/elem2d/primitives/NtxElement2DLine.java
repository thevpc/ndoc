package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.NDocArrow;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveType;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;

public class NtxElement2DLine extends AbstractNtxElement2DPrimitive {
    private NTxPoint2D from;
    private NTxPoint2D to;
    private NDocArrow startArrow;
    private NDocArrow endArrow;

    public NtxElement2DLine(NTxPoint2D from, NTxPoint2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public NTxElement2DPrimitiveType type() {
        return NTxElement2DPrimitiveType.LINE;
    }

    public NTxPoint2D getFrom() {
        return from;
    }

    public NtxElement2DLine setFrom(NTxPoint2D from) {
        this.from = from;
        return this;
    }

    public NTxPoint2D getTo() {
        return to;
    }

    public NtxElement2DLine setTo(NTxPoint2D to) {
        this.to = to;
        return this;
    }

    public NDocArrow getStartArrow() {
        return startArrow;
    }

    public NtxElement2DLine setStartArrow(NDocArrow startType) {
        this.startArrow = startType;
        return this;
    }

    public NDocArrow getEndArrow() {
        return endArrow;
    }

    public NtxElement2DLine setEndArrow(NDocArrow toType) {
        this.endArrow = toType;
        return this;
    }
}
