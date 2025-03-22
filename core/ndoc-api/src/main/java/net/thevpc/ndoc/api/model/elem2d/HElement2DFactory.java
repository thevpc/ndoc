package net.thevpc.ndoc.api.model.elem2d;

import net.thevpc.ndoc.api.model.elem2d.primitives.*;

public class HElement2DFactory {
    public static HElement2DLine line(HPoint2D from, HPoint2D to) {
        return new HElement2DLine(from, to);
    }

    public static HElement2DQuadCurve quad(HPoint2D from, HPoint2D ctrl, HPoint2D to) {
        return new HElement2DQuadCurve(from, to).setCtrl(ctrl);
    }

    public static HElement2DCubicCurve cubic(HPoint2D from, HPoint2D ctrl1, HPoint2D ctrl2, HPoint2D to) {
        return new HElement2DCubicCurve(from, to).setCtrl1(ctrl1).setCtrl2(ctrl2);
    }

    public static HElement2DPolygon polygon(HPoint2D... points) {
        return new HElement2DPolygon(points, true, true);
    }

    public static HElement2DPolyline polyline(HPoint2D... points) {
        return new HElement2DPolyline(points);
    }
}
