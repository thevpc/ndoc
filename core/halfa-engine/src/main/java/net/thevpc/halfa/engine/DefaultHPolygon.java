package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HEllipse;
import net.thevpc.halfa.api.model.HPolygon;

import java.awt.geom.Point2D;

public class DefaultHPolygon extends AbstractHPagePart implements HPolygon {
    private Point2D.Double[] points;

    public DefaultHPolygon(Point2D.Double[] points) {
        this.points = points;
    }


    public Point2D.Double[] points() {
        return points;
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.POLYGON;
    }
}
