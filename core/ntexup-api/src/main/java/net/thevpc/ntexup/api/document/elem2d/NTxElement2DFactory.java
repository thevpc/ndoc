package net.thevpc.ntexup.api.document.elem2d;


import net.thevpc.ntexup.api.document.elem2d.primitives.*;

public class NTxElement2DFactory {
    public static NtxElement2DLine line(NTxPoint2D from, NTxPoint2D to) {
        return new NtxElement2DLine(from, to);
    }

    public static NtxElement2DQuadCurve quad(NTxPoint2D from, NTxPoint2D ctrl, NTxPoint2D to) {
        return new NtxElement2DQuadCurve(from, to).setCtrl(ctrl);
    }

    public static NtxElement2DCubicCurve cubic(NTxPoint2D from, NTxPoint2D ctrl1, NTxPoint2D ctrl2, NTxPoint2D to) {
        return new NtxElement2DCubicCurve(from, to).setCtrl1(ctrl1).setCtrl2(ctrl2);
    }

    public static NtxElement2DPolygon polygon(NTxPoint2D... points) {
        return new NtxElement2DPolygon(points, true, true);
    }

    public static NtxElement2DPolyline polyline(NTxPoint2D... points) {
        return new NtxElement2DPolyline(points);
    }
}
