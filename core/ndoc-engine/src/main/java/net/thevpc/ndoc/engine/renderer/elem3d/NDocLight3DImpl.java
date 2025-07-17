package net.thevpc.ndoc.engine.renderer.elem3d;

import net.thevpc.ndoc.api.model.elem3d.NDocLight3D;
import net.thevpc.ndoc.api.model.elem3d.NDocVector3D;

public class NDocLight3DImpl implements NDocLight3D, Cloneable {
    private NDocVector3D orientation = new NDocVector3D(0, 0, 1);

    public NDocVector3D orientation() {
        return orientation;
    }

    public NDocLight3DImpl setOrientation(NDocVector3D orientation) {
        NDocLight3DImpl n = (NDocLight3DImpl) copy();
        n.orientation = orientation == null ? new NDocVector3D(0, -1, 0) : orientation;
        return n;
    }

    public NDocLight3D copy() {
        try {
            return (NDocLight3D) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
