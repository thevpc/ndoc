package net.thevpc.ndoc.api.document.elem3d.primitives;

import net.thevpc.ndoc.api.document.elem3d.AbstractElement3DPrimitive;
import net.thevpc.ndoc.api.document.elem3d.NDocElement3DPrimitiveType;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;

public class NDocElement3DTriangle extends AbstractElement3DPrimitive {
    NDocPoint3D p1;
    NDocPoint3D p2;
    NDocPoint3D p3;
    boolean fill = false;
    boolean contour = false;

    public NDocElement3DTriangle(NDocPoint3D p1, NDocPoint3D p2, NDocPoint3D p3, boolean fill, boolean contour) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.fill = fill;
        this.contour = contour;
    }

    public NDocElement3DTriangle setFilled(boolean filled) {
        this.fill = filled;
        return this;
    }

    public NDocElement3DTriangle setContour(boolean contour) {
        this.contour = contour;
        return this;
    }

    public NDocPoint3D getP1() {
        return p1;
    }

    public NDocPoint3D getP2() {
        return p2;
    }

    public NDocPoint3D getP3() {
        return p3;
    }

    public boolean isFill() {
        return fill;
    }

    public boolean isContour() {
        return contour;
    }

    @Override
    public NDocElement3DPrimitiveType type() {
        return NDocElement3DPrimitiveType.TRIANGLE;
    }


}
