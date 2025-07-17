package net.thevpc.ndoc.api.model.elem3d.primitives;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.model.elem3d.NDocElement3DPrimitiveType;
import net.thevpc.ndoc.api.model.elem3d.NDocPoint3D;

public class NDocElement3DArc extends AbstractElement3DPrimitive {
    private NDocPoint3D from;
    private NDocPoint3D to;
    private double startAngle;
    private double endAngle;

    public NDocElement3DArc(NDocPoint3D from, NDocPoint3D to, double startAngle, double endAngle) {
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

    public NDocPoint3D getFrom() {
        return from;
    }

    public NDocPoint3D getTo() {
        return to;
    }

    @Override
    public NDocElement3DPrimitiveType type() {
        return NDocElement3DPrimitiveType.ARC;
    }
}
