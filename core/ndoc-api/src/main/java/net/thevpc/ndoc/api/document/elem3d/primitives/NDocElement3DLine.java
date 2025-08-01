package net.thevpc.ndoc.api.document.elem3d.primitives;

import net.thevpc.ndoc.api.document.NDocArrow;
import net.thevpc.ndoc.api.document.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.document.elem3d.NDocElement3DPrimitiveType;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;

import java.util.Objects;

public class NDocElement3DLine extends AbstractElement3DPrimitive {
    private NDocPoint3D from;
    private NDocPoint3D to;
    private NDocArrow startArrow = null;
    private NDocArrow endArrow = null;

    public NDocElement3DLine(NDocPoint3D from, NDocPoint3D to) {
        this.from = from;
        this.to = to;
    }

    public NDocArrow getStartArrow() {
        return startArrow;
    }

    public NDocElement3DLine setStartArrow(NDocArrow startArrow) {
        this.startArrow = startArrow;
        return this;
    }

    public NDocArrow getEndArrow() {
        return endArrow;
    }

    public NDocElement3DLine setEndArrow(NDocArrow endArrow) {
        this.endArrow = endArrow;
        return this;
    }

    public NDocPoint3D getFrom() {
        return from;
    }

    public NDocPoint3D getTo() {
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
        NDocElement3DLine that = (NDocElement3DLine) o;
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
