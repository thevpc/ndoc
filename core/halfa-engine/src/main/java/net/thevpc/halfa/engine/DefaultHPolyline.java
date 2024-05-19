package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HPolygon;
import net.thevpc.halfa.api.model.HPolyline;

import java.awt.geom.Point2D;

public class DefaultHPolyline extends AbstractHPagePart implements HPolyline {
    private Point2D.Double[] points;

    public DefaultHPolyline(Point2D.Double[] points) {
        this.points = points;
    }

    public Point2D.Double[] points() {
        return points;
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.POLYLINE;
    }
}
