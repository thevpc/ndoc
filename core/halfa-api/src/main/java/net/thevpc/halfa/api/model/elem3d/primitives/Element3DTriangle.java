package net.thevpc.halfa.api.model.elem3d.primitives;

import net.thevpc.halfa.api.model.elem3d.AbstractElement3DPrimitive;
import net.thevpc.halfa.api.model.elem3d.Element3DPrimitiveType;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;

public class Element3DTriangle extends AbstractElement3DPrimitive {
    HPoint3D p1;
    HPoint3D p2;
    HPoint3D p3;
    boolean fill = false;
    boolean contour = false;

    public Element3DTriangle(HPoint3D p1, HPoint3D p2, HPoint3D p3, boolean fill, boolean contour) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.fill = fill;
        this.contour = contour;
    }

    public Element3DTriangle setFilled(boolean filled) {
        this.fill = filled;
        return this;
    }

    public Element3DTriangle setContour(boolean contour) {
        this.contour = contour;
        return this;
    }

    public HPoint3D getP1() {
        return p1;
    }

    public HPoint3D getP2() {
        return p2;
    }

    public HPoint3D getP3() {
        return p3;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isContour() {
        return contour;
    }

    @Override
    public Element3DPrimitiveType type() {
        return Element3DPrimitiveType.TRIANGLE;
    }


}
