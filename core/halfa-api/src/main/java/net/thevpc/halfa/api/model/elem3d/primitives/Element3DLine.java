package net.thevpc.halfa.api.model.elem3d.primitives;

import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.Element3DPrimitiveType;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;

import java.util.Objects;

public class Element3DLine extends AbstractElement3DPrimitive {
    private HPoint3D from;
    private HPoint3D to;
    private HArrayHead startType = HArrayHead.NONE;
    private HArrayHead endType = HArrayHead.NONE;

    public Element3DLine(HPoint3D from, HPoint3D to) {
        this.from = from;
        this.to = to;
    }

    public HArrayHead getStartType() {
        return startType;
    }

    public Element3DLine setStartType(HArrayHead startType) {
        this.startType = startType;
        return this;
    }

    public HArrayHead getEndType() {
        return endType;
    }

    public Element3DLine setEndType(HArrayHead endType) {
        this.endType = endType;
        return this;
    }

    public HPoint3D getFrom() {
        return from;
    }

    public HPoint3D getTo() {
        return to;
    }

    @Override
    public Element3DPrimitiveType type() {
        return Element3DPrimitiveType.LINE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element3DLine that = (Element3DLine) o;
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
