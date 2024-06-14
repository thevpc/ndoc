package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DPolyline;
import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DPolygon;
import net.thevpc.halfa.api.model.elem2d.primitives.HElement2DLine;

public class HElement2DFactory {
    public static HElement2DLine line(HPoint2D from, HPoint2D to) {
        return new HElement2DLine(from, to);
    }

    public static HElement2DPolygon polygon(HPoint2D... points) {
        return new HElement2DPolygon(points, true, true);
    }

    public static HElement2DPolyline polyline(HPoint2D... points) {
        return new HElement2DPolyline(points);
    }
}
