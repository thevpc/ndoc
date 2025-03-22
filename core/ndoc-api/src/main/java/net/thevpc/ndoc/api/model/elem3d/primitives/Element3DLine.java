package net.thevpc.ndoc.api.model.elem3d.primitives;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.Element3DPrimitiveType;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;

import java.util.Objects;

public class Element3DLine extends AbstractElement3DPrimitive {
    private HPoint3D from;
    private HPoint3D to;
    private HArrow startArrow = null;
    private HArrow endArrow = null;

    public Element3DLine(HPoint3D from, HPoint3D to) {
        this.from = from;
        this.to = to;
    }

    public HArrow getStartArrow() {
        return startArrow;
    }

    public Element3DLine setStartArrow(HArrow startArrow) {
        this.startArrow = startArrow;
        return this;
    }

    public HArrow getEndArrow() {
        return endArrow;
    }

    public Element3DLine setEndArrow(HArrow endArrow) {
        this.endArrow = endArrow;
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
