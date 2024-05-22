package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.TsonSer;
import net.thevpc.tson.TsonElement;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HPolylineImpl extends AbstractHNode implements HPolyline {
    private List<Point2D.Double> points;

    public HPolylineImpl(Point2D.Double... points) {
        this.points = new ArrayList<>(Arrays.asList(points));
    }

    @Override
    public HPolyline add(Point2D.Double d) {
        if (d != null) {
            points.add(d);
        }
        return this;
    }
    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HPolyline) {
                HPolyline t = (HPolyline) other;
                for (Point2D.Double point : t.points()) {
                    add(point);
                }
            }
        }
    }

    public Point2D.Double[] points() {
        return points.toArray(new Point2D.Double[0]);
    }

    @Override
    public HNodeType type() {
        return HNodeType.POLYLINE;
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this).addChildren(TsonSer.toTson(points()))
                .build();
    }
}
