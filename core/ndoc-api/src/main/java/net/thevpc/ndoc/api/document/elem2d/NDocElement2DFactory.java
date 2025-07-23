package net.thevpc.ndoc.api.document.elem2d;


import net.thevpc.ndoc.api.document.elem2d.primitives.*;

public class NDocElement2DFactory {
    public static NDocElement2DLine line(NDocPoint2D from, NDocPoint2D to) {
        return new NDocElement2DLine(from, to);
    }

    public static NDocElement2DQuadCurve quad(NDocPoint2D from, NDocPoint2D ctrl, NDocPoint2D to) {
        return new NDocElement2DQuadCurve(from, to).setCtrl(ctrl);
    }

    public static NDocElement2DCubicCurve cubic(NDocPoint2D from, NDocPoint2D ctrl1, NDocPoint2D ctrl2, NDocPoint2D to) {
        return new NDocElement2DCubicCurve(from, to).setCtrl1(ctrl1).setCtrl2(ctrl2);
    }

    public static NDocElement2DPolygon polygon(NDocPoint2D... points) {
        return new NDocElement2DPolygon(points, true, true);
    }

    public static NDocElement2DPolyline polyline(NDocPoint2D... points) {
        return new NDocElement2DPolyline(points);
    }
}
