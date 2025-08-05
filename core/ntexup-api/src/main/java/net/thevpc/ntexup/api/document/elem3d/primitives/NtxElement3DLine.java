package net.thevpc.ntexup.api.document.elem3d.primitives;

import net.thevpc.ntexup.api.document.NDocArrow;
import net.thevpc.ntexup.api.document.elem3d.AbstractNTxElement3DPrimitive;
import net.thevpc.ntexup.api.document.elem3d.NDocElement3DPrimitiveType;
import net.thevpc.ntexup.api.document.elem3d.NTxPoint3D;

import java.util.Objects;

public class NtxElement3DLine extends AbstractNTxElement3DPrimitive {
    private NTxPoint3D from;
    private NTxPoint3D to;
    private NDocArrow startArrow = null;
    private NDocArrow endArrow = null;

    public NtxElement3DLine(NTxPoint3D from, NTxPoint3D to) {
        this.from = from;
        this.to = to;
    }

    public NDocArrow getStartArrow() {
        return startArrow;
    }

    public NtxElement3DLine setStartArrow(NDocArrow startArrow) {
        this.startArrow = startArrow;
        return this;
    }

    public NDocArrow getEndArrow() {
        return endArrow;
    }

    public NtxElement3DLine setEndArrow(NDocArrow endArrow) {
        this.endArrow = endArrow;
        return this;
    }

    public NTxPoint3D getFrom() {
        return from;
    }

    public NTxPoint3D getTo() {
        return to;
    }

    @Override
    public NDocElement3DPrimitiveType type() {
        return NDocElement3DPrimitiveType.LINE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NtxElement3DLine that = (NtxElement3DLine) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Element3DLine{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
