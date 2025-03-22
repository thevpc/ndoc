package net.thevpc.ndoc.api.model.elem3d.composite;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3D;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;

public class Element3DSurface extends AbstractElement3D {
    private HPoint3D[] points;

    public Element3DSurface(HPoint3D... points) {
        this.points = points;
    }

    public HPoint3D[] getPoints() {
        return points;
    }

}
