package net.thevpc.ntexup.api.document.elem3d.composite;

import net.thevpc.ntexup.api.document.elem3d.AbstractNTxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NTxPoint3D;

public class NtxElement3DSurface extends AbstractNTxElement3D {
    private NTxPoint3D[] points;

    public NtxElement3DSurface(NTxPoint3D... points) {
        this.points = points;
    }

    public NTxPoint3D[] getPoints() {
        return points;
    }

}
