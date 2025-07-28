package net.thevpc.ndoc.api.document.elem3d.composite;

import net.thevpc.ndoc.api.document.elem3d.AbstractElement3D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;

public class NDocElement3DSurface extends AbstractElement3D {
    private NDocPoint3D[] points;

    public NDocElement3DSurface(NDocPoint3D... points) {
        this.points = points;
    }

    public NDocPoint3D[] getPoints() {
        return points;
    }

}
