package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.elem2d.NTxElement2DPrimitiveType;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;

public class NtxElement2DPolyline extends AbstractNtxElement2DPrimitive {
    private NTxPoint2D[] nodes;

    public NtxElement2DPolyline(NTxPoint2D[] nodes) {
        this.nodes = nodes;
    }

    public NTxPoint2D[] getNodes() {
        return nodes;
    }

    @Override
    public NTxElement2DPrimitiveType type() {
        return NTxElement2DPrimitiveType.POLYLINE;
    }
}
