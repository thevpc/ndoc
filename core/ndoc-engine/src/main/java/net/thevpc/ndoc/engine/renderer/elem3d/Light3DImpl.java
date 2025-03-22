package net.thevpc.ndoc.engine.renderer.elem3d;

import net.thevpc.ndoc.api.model.elem3d.Light3D;
import net.thevpc.ndoc.api.model.elem3d.HVector3D;

public class Light3DImpl implements Light3D, Cloneable {
    private HVector3D orientation = new HVector3D(0, 0, 1);

    public HVector3D orientation() {
        return orientation;
    }

    public Light3DImpl setOrientation(HVector3D orientation) {
        Light3DImpl n = (Light3DImpl) copy();
        n.orientation = orientation == null ? new HVector3D(0, -1, 0) : orientation;
        return n;
    }

    public Light3D copy() {
        try {
            return (Light3D) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
