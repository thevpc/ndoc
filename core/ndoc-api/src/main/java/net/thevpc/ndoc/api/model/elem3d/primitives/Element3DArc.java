package net.thevpc.ndoc.api.model.elem3d.primitives;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.Element3DPrimitiveType;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;

public class Element3DArc extends AbstractElement3DPrimitive {
    private HPoint3D from;
    private HPoint3D to;
    private double startAngle;
    private double endAngle;

    public Element3DArc(HPoint3D from, HPoint3D to, double startAngle, double endAngle) {
        this.from = from;
        this.to = to;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getEndAngle() {
        return endAngle;
    }

    public HPoint3D getFrom() {
        return from;
    }

    public HPoint3D getTo() {
        return to;
    }

    @Override
    public Element3DPrimitiveType type() {
        return Element3DPrimitiveType.ARC;
    }
}
